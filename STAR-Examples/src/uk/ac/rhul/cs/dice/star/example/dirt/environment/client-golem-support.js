//serverUrl = "http://localhost:8080/WebAppRunner/CleaningRobot";
serverUrl = window.location;

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
	if (typeName == "dirt") {
	Golem.request("add" + typeName, params, callback);
	}else{
		var url = window.location.href.replace("//", "/");
		url = url.split("/");
		var container = url[url.length-2];

		$.get(
		        serverUrl+"?task=addagent?" + params
		    ).done(function(response){
		    	$.post(
				        './'+'sweep'+'?'+params, 
				        callback
				    ).done(function(response){
				    	
				    	$.get(
				        './'+response+'?'+params
				        );
				        
				    }).fail(function(response) {
					alert('Could not create agent through the STAR API. This functionality is not support in development mode using the STAR SDK.')
					});
		    	
		    });
		
		
	}	
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

