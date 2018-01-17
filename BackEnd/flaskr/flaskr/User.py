# all the import
import json
import Item
import sys
import os
#dir_path = os.path.dirname(os.path.realpath(__file__))
#sys.stdout = open('/var/log/httpd/user.log','w')
#print("testing")

def getItemImage(theItem):
	image = Item.getImage(theItem["itemID"])
	theItem.update({"picture":image})
	return theItem

class User():
	def __init__(self, info, DB):
		self.id = info["userID"]
		self.DB = DB
		self.info = info

	# return boolean results
	def isExist(self):
		result_dict = {};
		try:
			conn = self.DB.connect()
			cursor = conn.cursor()
			cursor.callproc('spIfExist', (self.id,))
			result = cursor.fetchall()
			result_dict["exist"] = bool(result[0][0])
			result_dict["result"] = "true"

		except Exception as e:
			print("ERROR: isExist():"+str(e))
			result_dict["result"] = "false"
			result_dict["exist"] = False

		return result_dict

	# return dictionary result
	def getProfile(self):
		try:
			conn = self.DB.connect()
			cursor = conn.cursor()
			cursor.callproc('spGetUserProfile', (self.id,))
			result_dict =  json.loads(cursor.fetchall()[0][0])
			result_dict.update({"result":"true"})

		except Exception as e:
			print("ERROR: getProfile():"+str(e))
			result_dict = {"result":"false", "exist":"false"}

		return result_dict

	# return inventory list
	def getInventory(self):
		try:
			result_dict = {}
			conn = self.DB.connect()
			cursor = conn.cursor()
			cursor.callproc('spGetInventory', (self.id,))
			result = cursor.fetchall()
			itemlist = []
			for layer in result:
				for element in layer:
					# Add image entry into every item
					theItem = json.loads(element)
					theItem =  getItemImage(theItem)
					itemlist.append(theItem)
			result_dict.update({"result":"true","itemlist":itemlist})

		except Exception as e:
			print("ERROR: getInventory():"+str(e))
			result_dict = {"result":"false"}
			
		# Let's get user name and image url
		user_profile = self.getProfile()
		result_dict.update(user_profile)


		return result_dict

	# return inventory list
	def getBrowsing(self):
		try:
			result_dict = {}
			conn = self.DB.connect()
			cursor = conn.cursor()
			cursor.callproc('spSearchItem', (self.id, self.info["itemCategory"],self.info["location"]))
			result = cursor.fetchall()
			itemlist = []
			for layer in result:
				for element in layer:
					# Add image entry into every item
					theItem = json.loads(element)
					theItem =  getItemImage(theItem)
					itemlist.append(theItem)
			result_dict.update({"result":"true","itemlist":itemlist})

		except Exception as e:
			print("ERROR: getBrowsing():"+str(e))
			result_dict = {"result":"false"}

		return result_dict

	def write2DB(self):
		# write to DB function
		result_dict = None;
		try:
			conn = self.DB.connect()
			cursor = conn.cursor()
			cursor.callproc('spCreateUser', (self.id, self.name, self.email, self.phone, self.location, self.ImageURL))
			result = cursor.fetchall()
			conn.commit()
			result_dict =  {"result":"true"}

		except Exception as e:
			print("ERROR: write2DB():"+str(e))
			result_dict = {"result":"false"}

		return result_dict

	def updataProfile(self):
		result_dict = None

		self.name = self.info['userName']
		self.email = self.info['email']
		self.phone = self.info['phone']
		self.location = self.info['location']
		self.ImageURL = self.info['ImageURL']
		result_dict = self.write2DB()

		return result_dict


	def deleteUser(self):
		result_dict = None
		try:
			conn = self.DB.connect()
			cursor = conn.cursor()
			cursor.callproc('spDeleteUser', (self.id,))
			result = cursor.fetchall()
			conn.commit()
			result_dict =  {"result":"true"}

		except Exception as e:
			print("ERROR: deleteUser()):"+str(e))
			result_dict = {"result":"false"}

		return result_dict



