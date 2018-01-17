package com.example.yvonnewu.frontend;

/**
 * Created by Yvonne Wu on 10/23/2017.
 */

public final class JSONTags
{
    // tags for JSON received data
    static final String RESULT = "result";
    static final String EXIST = "exist";

    //tag for URL
    static final String URL = "http://ec2-35-182-143-240.ca-central-1.compute.amazonaws.com";
    //static final String URL = "http://128.189.247.21:8000";

    //tag for user related information
    static final String USERID = "userID";
    static final String USERNAME = "userName";
    static final String EMAIL = "email";
    static final String PHONE = "phone";
    static final String LOCATION = "location";
    static final String IMAGEURL = "ImageURL";

    //tag for item related information
    static final String ITEMID = "itemID";
    static final String ITEMTITLE = "ItemName";
    static final String ITEMCATEGORY = "itemCategory";
    static final String ITEMDESCRIPTION= "itemDescription";
    static final String ITEMPIC = "picture";
    static final String ITEMLIST = "itemlist";

    //tag for other sending JSON
    static final String TAG = "tag";
    static final String CREATE = "create";
    static final String INQUIRY =   "inquiry";

    //tag for values that are constant
    static final String TRUE = "true";
    static final String FALSE = "false";
    static final String NULL = "null";


    //tag for differentiate where the Json is from
    static final String FROM = "from";
    static final String PROFILE = "profile";
    static final String INVENTORY = "inventory";
    static final String DELETEITEM = "deleteItem";
    static final String SELECTION = "selection";
    static final String OFFERCENTER = "offerCenter";
    static final String ACCPETOFFER = "acceptOffer";
    static final String DECLINEOFFER = "declineOffer";
    static final String SEARCH = "search";

    //tag for making offer
    static final String FROMUSER = "fromUser";
    static final String TOUSER = "toUser";
    static final String USERTOPICK = "userToPick";
    static final String ITEMTOBARTER = "itemToBarter";
    static final String PREVIOUS = "previous";
    static final String OFFERNAME = "offerName";
    static final String OFFERSTATUS = "offerStatus";
    static final String PENDING = "Pending";
    static final String ACCEPT = "Accept";
    static final String DECLINE = "Decline";
    static final String NEEDRSP = "needRsp";
    static final String CONTACTINFO = "contactInfo";
    static final String CHILDOFFER = "children";
    static final String OFFERLIST = "offerList";
    static final String OFFERID = "offerID";
    static final String BRANDNEW = "BrandNew";
}
