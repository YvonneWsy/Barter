import os
import barterServer
import unittest
import tempfile
import json

test_user_info1 = dict(userID='1232334234',userName='testUser1',email='testUser1@testmail.com',
		phone='124456778', location='testplace1',ImageURL='http://justaurl.com')

test_user_info2 = dict(userID='234523456', userName = 'testUser2', email='testUser2@testmail.com', 
	phone='23445778', location='testplace2', ImageURL='http://justaurlasdf.com')

test_item_info1 = dict(userName='testUser1', itemName='testItem1',itemDescription='This is a test item description1.',
		picture='thisshouldbeabitmap1', itemCategory='Electronics', location='testplace1')

test_item_info2 = dict(userName='testUser2', itemName='testItem2',itemDescription='This is a test item description2.',
		picture='thisshouldbeabitmap2', itemCategory='Electronics', location='testplace2')

#TODO: Implement delete user function, otherwise test doesn't work
class FlaskrTestCase(unittest.TestCase):
	
	def setUp(self):
		barterServer.app.testing = True
		self.app = barterServer.app.test_client()

# ------------------------------------Test for user login----------------------
	def test_login_new(self):
		self.test_delete_user()
		content = json.dumps(dict(userID=test_user_info1["userID"]))
		headers = {"content-type":"application/json"}
		response = self.app.post("/", data=content, headers=headers)

		dic_res = json.loads(response.data)
		assert response.status_code == 200
		assert {"result":"true","exist":False} == dic_res

# No manual exception can be set for isExist() in db
	def test_login_new_exception(self):
		self.test_delete_user()
		content = json.dumps(dict(userID=''))
		headers = {"content-type":"application/json"}
		response = self.app.post("/", data=content, headers=headers)
		
		dic_res = json.loads(response.data)
		assert response.status_code == 200
		assert "false" == dic_res["result"]

	def test_login_old(self):
		# let's make sure 
		self.test_create_user()
		content = json.dumps(dict(userID=test_user_info1["userID"]))
		headers = {"content-type":"application/json"}
		response = self.app.post("/", data=content, headers=headers)
		dic_res = json.loads(response.data)
		assert response.status_code == 200
		assert {'ImageURL': test_user_info1['ImageURL'], 
		'email': test_user_info1['email'], 'exist': True, 
		'location': test_user_info1['location'], 'phone': test_user_info1['phone'], 
		'result': 'true', 'userName': test_user_info1['userName']} == dic_res

	def test_create_user(self, data = test_user_info1 ):
		data.update({"tag":"create"})
		content = json.dumps(data)
		headers = {"content-type":"application/json"}
		response = self.app.post("/profile", data=content, headers=headers)

		dic_res = json.loads(response.data)
		assert response.status_code == 200
		assert {"result":"true","from":"profile"} == dic_res

	def test_create_user_exception(self):
		self.test_create_user()
		data = test_user_info1
		data.update({"tag":"create"})
		content = json.dumps(data)
		headers = {"content-type":"application/json"}
		response = self.app.post("/profile", data=content, headers=headers)

		dic_res = json.loads(response.data)
		assert response.status_code == 200
		assert {"result":"true","from":"profile"} == dic_res

	def test_get_profile(self):
		self.test_create_user()
		data = {"userID":test_user_info1["userID"]}
		data.update({"tag":"inquiry"})
		content = json.dumps(data)
		headers = {"content-type":"application/json"}
		response = self.app.post("/profile", data=content, headers=headers)

		dic_res = json.loads(response.data)
		assert response.status_code == 200
		assert "true" == dic_res["result"]
		assert "profile" == dic_res["from"]
		assert test_user_info1["userName"] == dic_res["userName"]
		assert test_user_info1["location"] == dic_res["location"]		
		assert test_user_info1["ImageURL"] == dic_res["ImageURL"]
		assert test_user_info1["email"] == dic_res["email"]
		assert test_user_info1["phone"] == dic_res["phone"]

	def test_delete_user(self):
		self.test_create_user()
		content = json.dumps(dict(userID=test_user_info1["userID"]))
		headers = {"content-type":"application/json"}
		response = self.app.post("/deleteUser", data=content, headers=headers)

		dic_res = json.loads(response.data)
		assert response.status_code == 200
		assert {"result":"true","from":"deleteUser"} == dic_res

	def test_delete_user_exception(self):
		self.test_create_user()
		content = json.dumps(dict(userID=''))
		headers = {"content-type":"application/json"}
		response = self.app.post("/deleteUser", data=content, headers=headers)

		dic_res = json.loads(response.data)
		assert response.status_code == 200
		assert {"result":"false","from":"deleteUser"} == dic_res

# -------------------------------------Test for item-------------------------------------------
	def test_add_item(self):
		self.test_delete_user()
		self.test_create_user()
		content = json.dumps(dict(userID=test_user_info1["userID"],itemName=test_item_info1["itemName"],itemDescription=test_item_info1["itemDescription"],
		picture=test_item_info1["picture"], itemCategory=test_item_info1["itemCategory"]))
		headers = {"content-type":"application/json"}
		response = self.app.post("/addItem", data=content, headers=headers)

		dic_res = json.loads(response.data)
		test_item_info1.update({"itemID":dic_res["itemID"]})
		assert response.status_code == 200
		assert "true" == dic_res["result"]
		self.assertTrue(dic_res["itemID"])

	def test_add_item_exception(self):
		self.test_delete_user()
		content = json.dumps(dict(userID=test_user_info1["userID"],itemName=test_item_info1["itemName"],itemDescription=test_item_info1["itemDescription"],
		picture=test_item_info1["picture"], itemCategory=test_item_info1["itemCategory"]))
		headers = {"content-type":"application/json"}
		response = self.app.post("/addItem", data=content, headers=headers)

		dic_res = json.loads(response.data)
		assert response.status_code == 200
		assert "false" == dic_res["result"]

	def test_delete_item(self):
		self.test_create_user()
		self.test_add_item()
		content = json.dumps(dict(itemID='1232334234'))
		headers = {"content-type":"application/json"}
		response = self.app.post("/deleteItem", data=content, headers=headers)

		dic_res = json.loads(response.data)
		assert response.status_code == 200
		assert "deleteItem" == dic_res["from"]
		self.assertTrue(dic_res["result"])

	def test_delete_item_exception(self):
		content = json.dumps(dict(itemID=''))
		headers = {"content-type":"application/json"}
		response = self.app.post("/deleteItem", data=content, headers=headers)

		dic_res = json.loads(response.data)
		assert response.status_code == 200
		assert "deleteItem" == dic_res["from"]
		assert "false" == dic_res["result"]

	def test_get_item(self):
		self.test_create_user()
		self.test_add_item()
		content = json.dumps(dict(itemID=test_item_info1["itemID"]))
		headers = {"content-type":"application/json"}
		response = self.app.post("/item", data=content, headers=headers)

		dic_res = json.loads(response.data)
		assert response.status_code == 200
		assert "true" == dic_res["result"]
		assert "item" == dic_res["from"]
		assert test_item_info1["itemName"] == dic_res["ItemName"]
		assert test_item_info1["itemDescription"] == dic_res["itemDescription"]		
		assert test_item_info1["itemCategory"] == dic_res["itemCategory"]
		assert test_item_info1["picture"] == dic_res["picture"]
		assert test_item_info1["location"] == dic_res["itemLocation"]

	def test_get_item_exception(self):
		self.test_delete_user()
		content = json.dumps(dict(itemID=test_item_info1["itemID"]))
		headers = {"content-type":"application/json"}
		response = self.app.post("/item", data=content, headers=headers)

		dic_res = json.loads(response.data)
		assert response.status_code == 200
		assert "false" == dic_res["result"]
		assert "item" == dic_res["from"]


# ----------------Test for inventory & browsing-------------------------------------------
	def test_inventory(self):
		self.test_create_user()
		self.test_add_item()
		content = json.dumps(dict(userID=test_user_info1["userID"]))
		headers = {"content-type":"application/json"}
		response = self.app.post("/inventory", data=content, headers=headers)

		dic_res = json.loads(response.data)
		assert response.status_code == 200
		assert "inventory" == dic_res["from"]
		self.assertTrue(dic_res["result"])
		self.assertTrue(dic_res["itemlist"])

	def test_inventory_exception(self):
		content = json.dumps(dict(userID=''))
		headers = {"content-type":"application/json"}
		response = self.app.post("/inventory", data=content, headers=headers)

		dic_res = json.loads(response.data)
		assert response.status_code == 200
		assert "inventory" == dic_res["from"]
		assert "false" == dic_res["result"]

	def test_browsing(self):
		self.test_create_user()
		self.test_add_item()
		content = json.dumps(dict(userID=test_user_info1["userID"], 
			itemCategory='Null',location='Null'))
		headers = {"content-type":"application/json"}
		response = self.app.post("/browsing", data=content, headers=headers)

		dic_res = json.loads(response.data)
		assert response.status_code == 200
		assert "browsing" == dic_res["from"]
		self.assertTrue(dic_res["result"])
		self.assertTrue(dic_res["itemlist"])

	def test_browsing_exception(self):
		content = json.dumps(dict(userID='', 
			itemCategory=1,location=1))
		headers = {"content-type":"application/json"}
		response = self.app.post("/browsing", data=content, headers=headers)

		dic_res = json.loads(response.data)
		assert response.status_code == 200
		assert "browsing" == dic_res["from"]
		assert "false" == dic_res["result"]

	# def test_makeOffer(self):
	# 	self.test_create_user();
	# 	self.test_create_user(self.test_user_info2);



if __name__ == '__main__':
	unittest.main()
