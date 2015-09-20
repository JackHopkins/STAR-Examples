//serverUrl = "http://localhost:8080/WebAppRunner/CleaningRobot";
serverUrl = "../CleaningRobot";
//serverUrl = "fake_server.html";

function Golem() {};

Golem.initServer = function(params, callback)
{
    Golem.request("start", params, callback);
}

//gets all the events we haven't received so far
Golem.getEvents = function(callback)
{
	Golem.request("getevents", "", callback);
}

//Add entity
Golem.addEntity = function(typeName, params, callback)
{
	Golem.request("add" + typeName, params, callback);
}

//Move User
Golem.moveUser = function(px, py)
{
	Golem.request("moveuser", "x="+px+"&y="+py, undefined);
}

//Add Dirt
Golem.addDirt = function(id, px, py)
{
	Golem.request("adddirt", "id="+id+"&x="+px+"&y="+py, undefined);
}

//Remove Dirt
Golem.removeDirt = function(px, py)
{
	Golem.request("removedirt", "x="+px+"&y="+py, undefined);
}


//Terminate
Golem.terminate = function(callback)
{
	Golem.request("terminate", "", callback);
}


//Generic Golem request
Golem.request = function(task, params, callback)
{
	if(params!="") params = "&" + params;
	$.get(
        serverUrl+"?task=" + task + params, 
        callback
    );
}



function addEntityUI(kind, entityid, entityName)
{
	return addTab(kind, entityid, entityName);
}

//Gets the initial state of the app/config
function getInitialState()
{
	
}

