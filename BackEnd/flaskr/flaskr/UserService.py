# all the imports
from User import User
from Item import Item

USER_ID_LENGTH = 21
class UserService():
	def __init__(self, DB):
		self.DB = DB

	def profilePage(self, profileData):
		# create new user object
		newUser = User(profileData, self.DB)
		# if it's new user, create profile
		if (profileData["tag"] == "create"):
			result_dict = newUser.updataProfile()
		# return profile info and result
		elif (profileData["tag"] == "inquiry"):
			result_dict = newUser.getProfile()

		return result_dict

	def deleteUserPage(self, profileData):
		theUser = User(profileData, self.DB)
		result_dict = theUser.deleteUser()

		return result_dict

	def addItemPage(self, itemInfo):
		# create new item object
		newItem = Item(itemInfo, self.DB)
		result_dict = newItem.updateItem()

		return result_dict

# Gonna return new inventory back!!!
	def deleteItemPage(self, itemInfo):
		# create new user object
		theItem = Item(itemInfo, self.DB)
		result_dict = theItem.deleteItem()
		userID = {"userID":itemInfo["itemID"][0:USER_ID_LENGTH]}
		theUser = User(userID, self.DB)
		inventory_dict = theUser.getInventory()
		result_dict.update(inventory_dict)

		return result_dict

	def loginPage(self, userID):
		result = {"result":"true"}
		theUser = User(userID, self.DB)
		result_dict = theUser.isExist()
		# return profile for existed user
		if result_dict["exist"]:
			profile = theUser.getProfile()
			result_dict.update(profile)

		return result_dict

	def inventoryPage(self, userID):
		theUser = User(userID, self.DB)
		result_dict = theUser.getInventory()

		return result_dict

	def browsingPage(self, browsingInfo):
		theUser = User(browsingInfo, self.DB)
		result_dict = theUser.getBrowsing()

		return result_dict

	def itemPage(self, itemInfo):
		theItem = Item(itemInfo, self.DB)
		result_dict = theItem.getInfo()

		return result_dict



