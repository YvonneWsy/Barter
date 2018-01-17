# all the imports
import User
import Item

import linecache
import sys
import json

def PrintException():
	exc_type, exc_obj, tb = sys.exc_info()
	f = tb.tb_frame
	lineno = tb.tb_lineno
	filename = f.f_code.co_filename
	linecache.checkcache(filename)
	line = linecache.getline(filename, lineno, f.f_globals)
	print ('EXCEPTION IN ({}, LINE {} "{}"): {}'.format(filename, lineno, line.strip(), exc_obj))


class OfferService():
	def __init__(self, DB):
		self.DB = DB

	def makeOffer(self, offerInfo):
		# write to DB function
		result_dict = None;
		try:
			conn = self.DB.connect()
			cursor = conn.cursor()
			cursor.callproc('spCreateOffer', (offerInfo["fromUser"], offerInfo["toUser"], offerInfo["userToPick"], offerInfo["itemToBarter"], json.dumps(offerInfo["itemlist"]), offerInfo["previous"], offerInfo["offerName"]))
			result = cursor.fetchall()
			offerID = result[0][0]
			conn.commit()
			result_dict =  {"result":"true"}
			result_dict.update({"offerID":offerID})

		except Exception as e:
			#print("ERROR: offerService: makeOffer():"+str(e))
			PrintException()
			result_dict = {"result":"false"}

		return result_dict

	def acceptOffer(self,offerInfo):
		result_dict = {}
		fromContactInfo = {}
		try:
			userID = offerInfo["userID"]
			conn = self.DB.connect()
			cursor = conn.cursor()
			cursor.callproc('spAcceptOffer', (offerInfo["offerID"],))
			contactInfo =  json.loads(cursor.fetchall()[0][0])
			print("contactInfo")
			print(contactInfo)
			conn.commit()
			fromContactInfo.update({"phone":contactInfo["FromPhone"],
									"email":contactInfo["FromEmail"]})

			result_dict.update({"contactInfo":fromContactInfo})
			result_dict.update({"result":"true"})

		except Exception as e:
			PrintException()
			result_dict = {"result":"false"}

		return result_dict


	def declineOffer(self,offerInfo):
		result_dict = {}
		try:
			conn = self.DB.connect()
			cursor = conn.cursor()
			cursor.callproc('spDeclineOffer', (offerInfo["offerID"],))
			conn.commit()
			result =  cursor.fetchall()
			result_dict.update({"result":"true"})

		except Exception as e:
			PrintException()
			result_dict = {"result":"false"}

		return result_dict

	def getOffer(self, offerInfo):
		result_dict = {}
		item2Barter = {}

		try:
			userID = offerInfo["userID"]
			conn = self.DB.connect()
			cursor = conn.cursor()
			cursor.callproc('spGetOffer', (offerInfo["offerID"],))
			tmp = cursor.fetchall()
			result_dict =  json.loads(tmp[0][0])
			item2Barter.update({"itemID":result_dict["itemToBarter"]})

		except Exception as e:
			PrintException()
			result_dict = {"result":"false"}
			return result_dict
		
		# Get images for all items
		itemList = []
		for theItem in result_dict["itemList"]:	
			# Add image entry into every item
			try:
				theItem =  User.getItemImage(theItem)
			except Exception as e:
				theItem.update({"picture":"null"})
				PrintException()
			itemList.append(theItem)

		try:
			item2Barter=  User.getItemImage(item2Barter)
		except Exception as e:
			item2Barter.update({"picture":"null"})
			PrintException()

		result_dict.update({"result":"true","itemlist":itemList,"itemToBarter":item2Barter})

		# Update offer status based on userID
		if result_dict["offerStatus"] == "Open":
			if userID == result_dict["fromUser"]:
				result_dict["offerStatus"] = "Pending"
			elif userID == result_dict["toUser"]:
				result_dict["offerStatus"] = "needRsp"

		# Updata contact info based on userID
		elif result_dict["offerStatus"] == "Accept":
			contactInfo = {}
			try:
				if (result_dict["fromUser"] == userID):
					toUserInfo = json.loads(tmp[0][0])
					print("toUserInfo")
					print(toUserInfo)
					contactInfo["phone"] = toUserInfo["ToPhone"]
					contactInfo["email"] = toUserInfo["ToEmail"]

				elif(result_dict["toUser"] == userID):
					fromUserInfo = json.loads(tmp[0][0])
					print("fromUserInfo")
					print(fromUserInfo)
					contactInfo["phone"] = fromUserInfo["FromPhone"]
					contactInfo["email"] = fromUserInfo["FromEmail"]

				result_dict.update({"contactInfo":contactInfo})
			except Exception as e:
				PrintException()
				result_dict.update({"result":"false"})

		return result_dict

	def offerCenter(self, offerInfo):
		result_dict = {}
		parentOffer = []
		result_dict.update({"result":"true", "offerList":parentOffer})
		try:
			conn = self.DB.connect()
			cursor = conn.cursor()
			cursor.callproc('spOfferCenter', (offerInfo["userID"],'parent'))
			parentResult =  cursor.fetchall()
			
			for element in parentResult:
				for parent in element:
					# Get parent offer dict
					if parent:
						parent = json.loads(parent)
						# ask for children offers
						cursor.callproc('spOfferCenter', (parent["offerID"],'children'))
						childrenResult = cursor.fetchall()

						childrenOffer = []
						parent.update({"children": childrenOffer })
						for element in childrenResult:
							for children in element:
								if children:
									childrenOffer = json.loads(children)
									print(type(childrenOffer))
									for i in range(len(childrenOffer)):
										if childrenOffer[i]:
											childrenOffer[i] = json.loads(childrenOffer[i])
											

						parent.update({"children": childrenOffer })
						parentOffer.append(parent)

			#update results
			result_dict.update({"offerList":parentOffer})

		except Exception as e:
			PrintException()
			result_dict = {"result":"false"}

		return result_dict
