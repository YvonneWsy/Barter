# all the import
import json
import os

PIC_DIRECTORY = os.path.dirname(os.path.realpath(__file__))

def getImage(itemID):
	filePath = os.path.join(PIC_DIRECTORY,"image",itemID+".txt")

	try:
		with open(filePath,"r") as imageFile:
			result = imageFile.read().replace('\n', '')
	except Exception as e:
		print("ERROR: Item: getImage():"+str(e))
		result = "Null"

	return result

def deleteImage(itemID):
	filePath = os.path.join(PIC_DIRECTORY,"image",itemID+".txt")

	try:
		os.remove(filePath)
	except Exception as e:
		print("ERROR: Item: deleteImage():"+str(e))
		result = "Null"

	return result

class Item():
	def __init__(self, itemInfo, DB):
		self.DB = DB
		self.itemInfo = itemInfo
		self.itemID = None

	# return dictionary result
	def getInfo(self):
		result_dict = {}
		try:
			conn = self.DB.connect()
			cursor = conn.cursor()
			cursor.callproc('spGetItem', (self.itemInfo["itemID"],))
			result_dict =  json.loads(cursor.fetchall()[0][0])
			result_dict.update({"result":"true"})

		except Exception as e:
			print("ERROR: getInfo():"+str(e))
			result_dict = {"result":"false"}

		# Add image back
		result_dict.update({"picture":getImage(self.itemInfo["itemID"])})

		return result_dict

	def write2DB(self):
		# write to DB function
		result_dict = None;
		try:
			conn = self.DB.connect()
			cursor = conn.cursor()
			cursor.callproc('spAddItem', (self.userID, self.title, self.category, self.description, "Null"))
			result = cursor.fetchall()
			self.itemID = result[0][0]
			conn.commit()
			result_dict =  {"result":"true"}
			result_dict.update({"itemID":self.itemID})

		except Exception as e:
			print("ERROR: Item: write2DB():"+str(e))
			result_dict = {"result":"false"}

		return result_dict

	def updateItem(self):
		result_dict = None
		self.userID = self.itemInfo['userID']
		self.title = self.itemInfo['itemName']
		self.description = self.itemInfo['itemDescription']
		self.picture = self.itemInfo['picture']
		self.category = self.itemInfo['itemCategory']
		result_dict = self.write2DB()
		if(self.itemID):
			save = self.saveImage()
			if not save:
				result_dict.update({"result":"false"})
		return result_dict

	def deleteItem(self):
		result_dict = {"result":"false"}
		if("itemID" in self.itemInfo):
			try:
				conn = self.DB.connect()
				cursor = conn.cursor()
				cursor.callproc('spDeleteItem', (self.itemInfo["itemID"],))
				result = cursor.fetchall()
				conn.commit()
				if len(result):
					raise Exception(result)
				deleteImage(self.itemInfo["itemID"])	
				result_dict =  {"result":"true"}

			except Exception as e:
				print("ERROR: Item: deleteItem():"+str(e))
			
		return result_dict

	def saveImage(self):
		filePath = os.path.join(PIC_DIRECTORY,"image/"+self.itemID+".txt")
		result = True
		try:
			imageFile =open(filePath,"w")
			imageFile.write(self.picture)
			imageFile.close()
		except Exception as e:
			print("ERROR: Item: saveImage():"+str(e))
			result = False

		return result
