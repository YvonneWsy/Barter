
from flask import Flask, request, jsonify, make_response
from flaskext.mysql import MySQL
from UserService import UserService
from OfferService import OfferService

import json
import setting


#TODO: add logging
#TODO: add blueprint
mysql = MySQL()
app = Flask(__name__)
app.config.from_object(setting)

mysql.init_app(app)
barterUserService = UserService(mysql)
offerService = OfferService(mysql)

# profile view
# update profile info in db
@app.route('/profile', methods=['POST'])
def profile():
	profileData = request.get_json(silent=True)
	result_dict = barterUserService.profilePage(profileData)
	result_dict.update({"from":"profile"})

	return jsonify(result_dict)

# profile view
# update profile info in db
@app.route('/deleteUser', methods=['POST'])
def deleteUser():
	profileData = request.get_json(silent=True)
	result_dict = barterUserService.deleteUserPage(profileData)
	result_dict.update({"from":"deleteUser"})
	return jsonify(result_dict)

# addItem view
# add item into db
@app.route('/addItem', methods=['POST'])
def addItem():
	itemInfo = request.get_json(silent=True)
	result_dict = barterUserService.addItemPage(itemInfo)
	return jsonify(result_dict)

# addItem view
# add item into db
@app.route('/deleteItem', methods=['POST'])
def deleteItem():
	itemInfo = request.get_json(silent=True)
	result_dict = barterUserService.deleteItemPage(itemInfo)
	result_dict.update({"from":"deleteItem"})
	return jsonify(result_dict)

# inventory view
# return inventory of the user
@app.route('/inventory', methods=['POST'])
def inventory():
	userID = request.get_json(silent=True)
	result_dict = barterUserService.inventoryPage(userID)

	if("tag" in userID):
		if userID["tag"] == "selection":
			result_dict.update({"from":"selection"})
	else:
		result_dict.update({"from":"inventory"})
	return jsonify(result_dict)

# browsering view
# return browsing result
@app.route('/browsing', methods=['POST'])
def browsing():
	browsingInfo = request.get_json(silent=True)
	result_dict = barterUserService.browsingPage(browsingInfo)
	result_dict.update({"from":"browsing"})
	print(result_dict)
	return jsonify(result_dict)

# item view
# return all details of the item
@app.route('/item', methods=['POST'])
def item():
	itemID = request.get_json(silent=True)
	result_dict = barterUserService.itemPage(itemID)
	result_dict.update({"from":"item"})
	return jsonify(result_dict)

# offer center view
# store offer info into DB
# return timestamp + offerID
@app.route('/offerCenter', methods=['POST'])
def offerCenter():
	offerInfo = request.get_json(silent=True)
	result_dict = offerService.offerCenter(offerInfo)
	result_dict.update({"from":"offerCenter"})
	return jsonify(result_dict)

# one offer view
# store offer info into DB
# recieve offerID
# return from, to, itemList('name','id'), timestamp, offerStatus
@app.route('/offer', methods=['POST'])
def Offer():
	offerInfo = request.get_json(silent=True)
	result_dict = offerService.getOffer(offerInfo)
	result_dict.update({"from":"offer"})
	print(result_dict)
	return jsonify(result_dict)

# make offer view
# store offer info into DB
@app.route('/makeOffer', methods=['POST'])
def makeOffer():
	offerInfo = request.get_json(silent=True)
	result_dict = offerService.makeOffer(offerInfo)
	#result_dict = {"result":"true","offerList":[{"offerID":"273ef65ff0c97a15278f7262174d2d86e659cdfea1954867e47e05bcbada999f",
	#"offerName":"testOffer1","children":[{"offerID":"273ef65ff0c97a15278f7262174d2d86e659cdfea1954867e47e05bcbada990f", "offerName":"testOffer2"}]}]}
	result_dict.update({"from":"makeOffer"})
	return jsonify(result_dict)

# accept offer view
# store offer info into DB
@app.route('/acceptOffer', methods=['POST'])
def acceptOffer():
	offerInfo = request.get_json(silent=True)
	result_dict = offerService.acceptOffer(offerInfo)
	result_dict.update({"from":"acceptOffer"})
	return jsonify(result_dict)

# decline offer view
# store offer info into DB
@app.route('/declineOffer', methods=['POST'])
def declineOffer():
	offerInfo = request.get_json(silent=True)
	result_dict = offerService.declineOffer(offerInfo)
	result_dict.update({"from":"declineOffer"})
	return jsonify(result_dict)

# Home page
# Return profile to existed user:login -> inventory page
# Return false to new user:login -> profile
@app.route('/', methods=['POST'])
def login():
	userID = request.get_json(silent=True)
	response_dict = barterUserService.loginPage(userID)
	return jsonify(response_dict)

if __name__ == '__main__':
	app.run()

