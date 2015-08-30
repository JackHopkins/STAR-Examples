debug = true;
grid = undefined;
loopEvery = 200;
getEventsEvery = 2000;
guiInitialised = false;





////////////// GRID ENTITY ///////////////
function GridEntity(grid, id, name, type, px, py, orientation)
{
	this.grid = grid;
	this.id = id;
	this.name = name;
	this.type = type;
	this.px = px;
	this.py = py;
	//0: EAST, 1: SOUTH, 2: WEST, 3: NORTH
	this.orientation = 0;
	
	//GUI
	this.ui = undefined;
	this.element = undefined;
	this.container = undefined;
	this.eventQueueDisplay = undefined;
	this.pastEventsDisplay = undefined;
	
	//queue of the events
	//an event is an array of the format: [timestamp, target, action]
	//a new event is pushed to the end of the list, and execution is FIFO
	this.eventQueue = [];
	
	this.container = $('<div class="' + type.cssClass + ' tab-control" tab="' + id + '-tab" entityid="' + id + '"></div>');
	
	this.updateIcon();
	
	this.ui = addEntityUI(type.kind, id, name);
	this.setupUI();
}


GridEntity.prototype = {
    constructor: GridEntity,
    
    setupUI: function()
    {
    	tab = $(this.ui);
    	//Remove button (can't remove user)
    	if(this.type == window.entityTypes.dirt)
		{
	    	removeButton = $('<input type="button" value="remove" style="float: right">').appendTo(tab);
	    	$(removeButton).click({entity: this}, function(event) {
				entity = event.data.entity;
				grid.removeEntity(entity);
			});
		}
		
    	$('<img id="icon_' + this.id + '" src="' + this.type.guiIcon + '"/><h3>' + this.name + '</h3>').appendTo(tab);
    	
    	//Entity parameters
    	if(this.type != window.entityTypes.agent)
		{
	    	$('<h4>Move</h4>').appendTo(tab);
	    	
			controls = $('<div style="text-align:center"></div>').appendTo(tab);
			//up button
			btn = $('<input type="button" value="up">').appendTo(controls);
			$(btn).click({entity: this}, function(event) {
				entity = event.data.entity;
				if(entity.py>0 && grid.getEntityAt(entity.px, entity.py-1, window.entityTypes.dirt)==undefined)
				{
					entity.jump(entity.px, --entity.py);
				}
			});
			
			btn = $('<br/>').appendTo(controls);
			
			
			//left button
			btn = $('<input type="button" value="left">').appendTo(controls);
			$(btn).click({entity: this}, function(event) {
				entity = event.data.entity;
				if(entity.px>0 && grid.getEntityAt(entity.px-1, entity.py, window.entityTypes.dirt)==undefined)
				{
					entity.jump(--entity.px, entity.py);
				}
			});
			
			//right button
			btn = $('<input type="button" value="right">').appendTo(controls);
			$(btn).click({entity: this}, function(event) {
				entity = event.data.entity;
				if(entity.px<grid.width-1 && grid.getEntityAt(entity.px+1, entity.py, window.entityTypes.dirt)==undefined)
				{
					entity.jump(++entity.px, entity.py);
				}
			});
			
			btn = $('<br/>').appendTo(controls);
			
			//down button
			btn = $('<input type="button" value="down">').appendTo(controls);
			$(btn).click({entity: this}, function(event) {
				entity = event.data.entity;
				if(entity.py<grid.height-1 && grid.getEntityAt(entity.px, entity.py+1, window.entityTypes.dirt)==undefined)
				{
					entity.jump(entity.px, ++entity.py);
				}
			});
		}
		/*
		//Rotation controls for agents
		if(this.type == window.entityTypes.agent)
		{
			$('<h4>Rotate</h4>').appendTo(tab);
    	
			controls = $('<div style="text-align:center"></div>').appendTo(tab);
			//ccw
			btn = $('<input type="button" value="ccw">').appendTo(controls);
			$(btn).click({entity: this}, function(event) {
				entity = event.data.entity;
				entity.orientation --;
				if(entity.orientation<0) entity.orientation=3;
				entity.updateIcon();
			});
			
			//cw
			btn = $('<input type="button" value="cw">').appendTo(controls);
			$(btn).click({entity: this}, function(event) {
				entity = event.data.entity;
				entity.orientation ++;
				if(entity.orientation>3) entity.orientation=0;
				entity.updateIcon();
			});
		}*/
		if(this.type == window.entityTypes.agent || this.type == window.entityTypes.dirt)
		{
			//Event Queue
	        $('<h4>Event Queue</h4>').appendTo(tab);
			this.eventQueueDisplay = $('<div class="event-list event-queue"></div>').appendTo(tab);
	        
			//Past Events
	        $('<h4>Past Events</h4>').appendTo(tab);
			this.pastEventsDisplay = $('<div class="event-list"></div>').appendTo(tab);
		}
    },
    
    addEvent: function(ev)
    {
    	this.eventQueue.push(ev);
        this.drawEventQueue();
    },
    
    updateIcon: function()
    {
    	gridCell = grid.getGridCell(this.px, this.py);
    	maxWidth = gridCell.offsetWidth;
    	if(maxWidth>200) maxWidth=25;
    	this.container.html('<img height="auto" width="' + maxWidth + '" src="' + this.type.images[this.orientation] + '"/>');
    	$(this.container).appendTo(gridCell);
    },
    
    turnRight: function()
    {
    	this.orientation++;
    	if(this.orientation==4) this.orientation=0;
    	this.updateIcon();
    },
    
    turnLeft: function()
    {
    	this.orientation--;
    	if(this.orientation==-1) this.orientation=3;
    	this.updateIcon();
    },
    
    jump: function(px, py)
    {
    	// when moving dirt we need to remove the previous and add the new position
    	if(this.type==window.entityTypes.dirt) Golem.removeDirt(this.px, this.py);
    	
    	this.px = px;
    	this.py = py;
    	if(this.px<0) this.px=0;
    	if(this.px>=grid.width) this.px=grid.width-1;
    	if(this.py<0) this.py=0;
    	if(this.py>=grid.height) this.px=grid.height-1;
    	
    	// when moving a user we need to inform the server
    	if(this.type==window.entityTypes.user) Golem.moveUser(px, py);
    	
    	// now we add the new position
    	if(this.type==window.entityTypes.dirt) Golem.addDirt(this.id, this.px, this.py);
    	
    	this.updateIcon();
    },
    
    move: function()
    {
    	switch(this.orientation)
    	{
    		case 0: this.jump(++this.px, this.py); break;
    		case 1: this.jump(this.px, ++this.py); break;
    		case 2: this.jump(--this.px, this.py); break;
    		case 3: this.jump(this.px, --this.py); break;
    	}
    },
    
    clean: function()
    {
    	dirt = grid.getEntityOfTypeAt(this.px, this.py, window.entityTypes.dirt);
    	if(dirt != undefined) grid.removeEntity(dirt);
    },
    
    executeEvent: function()
	{
		if(this.eventQueue.length==0) return; //no events to be executed
    	ec = this.eventQueue.shift();
    	if(ec.length >= 3)
    	{
    		switch(ec[2])
    		{
    			case "TURN_RIGHT": this.turnRight(); break;
    			case "TURN_LEFT": this.turnLeft(); break;
    			case "MOVE": this.move(); break;
    			case "CLEAN": this.clean(); break;
    		}
            
            //display the event in the past events display
            this.appendEventToDisplay(ec, this.pastEventsDisplay, true);
            this.drawEventQueue();
    	}
	},
    
    drawEventQueue: function()
    {
        $(this.eventQueueDisplay).empty();
        for(i in this.eventQueue)
        {
            ev = this.eventQueue[i];
        	this.appendEventToDisplay(ev, this.eventQueueDisplay, false);
        }
    },
    
    appendEventToDisplay: function(ev, display, appendToTop)
    {
    	toAppend = $("<div>" + ev[0] + ", " + ev[2] + "</div>");
        if(!appendToTop) $(toAppend).appendTo(display);
        else $(toAppend).prependTo(display);
    }
}
    
    
    
    
////////////// GRID ///////////////
function Grid(container, width, height)
{
	grid = this;
	this.user = undefined;
	
	this.container = container;
	this.width = width;
	this.height = height;
    
    this.paused = false;
    this.serverInitialised = false;
	
	this.entities = [];
    
	this.setupGrid();
	this.resume();
}


Grid.prototype = {
    constructor: Grid,
    
    mainLoop: function()
    {
    	if(grid.paused) return;
    	//clear the queue displays
		
    	//execute one event on each entity
    	for(i=0; i<grid.entities.length; i++)
        {
        	entity = grid.entities[i];
            entity.executeEvent();
        }
    	
        if(!grid.paused) setTimeout(grid.mainLoop, loopEvery);
    },
    
    pause: function()
    {
    	$('#execution-status').html("paused");
    	grid.paused = true;
    },
    
    resume: function()
    {
    	$('#execution-status').html("playing");
    	grid.paused = false;
    	grid.mainLoop();
    	grid.getEvents();
    },
    
    setupGrid: function()
    {
    	$("#"+this.container).empty();
    	$('<div class="grid-canvas"/>').appendTo("#"+this.container);
		for(i = 0; i < this.height; i++)
		{
			row = $('<div class="grid-row"/>').appendTo(".grid-canvas");
			for(j = 0; j < this.width; j++)
			{
				cell = $('<div class="grid-cell"/>').appendTo(row);
				//Clicking on grid cell, moves the user to that place
				$(cell).click({x: j, y:i}, function(event){
					x=event.data.x;
					y=event.data.y;
					if(grid.getEntityAt(x, y)==undefined)
					{
						//log(x + " " + y);
						grid.user.jump(x, y);
						
					}
				});
			}
		}
		
		this.user = this.addEntity("user", "User", window.entityTypes.user, 0, 0);
		this.user.ui = $("#user-parameters");
		this.user.setupUI();
		
		setupGUI();
    },
    
    initServer: function()
	{
		agentPositions = "";
		//agent positions
		first=true;
		for(i in this.entities)
        {
            if(this.entities[i].type.typeName=="agent")
            {
            	if(!first) agentPositions += "-";
            	first = false;
            	agentPositions += this.entities[i].id + "," + this.entities[i].px + "," + this.entities[i].py;
            }
        }
        //dirt positions
        dirtPositions = "";
        first=true;
		for(i in this.entities)
        {
            if(this.entities[i].type.typeName=="dirt")
            {
            	if(!first) dirtPositions += "-";
            	first = false;
            	dirtPositions += this.entities[i].id + "," + this.entities[i].px + "," + this.entities[i].py;
            }
        }
		
		$.ajaxSetup ({
		    cache: false
		});
		
		// Init Server
		Golem.initServer("width=" + this.width + "&height=" + this.height + "&agents=" + agentPositions + "&dirt=" + dirtPositions,
            function(){grid.serverInitialised = true;}
        );
	},
	
	getGridCell: function(x, y)
	{
		if(y>=$(".grid-canvas").children().length || x>= $($(".grid-canvas").children()[y]).children().length) 
			return undefined;
		return $($(".grid-canvas").children()[y]).children()[x];
	},
	
	getEntityAt: function(x, y, toIgnore)
	{
		for(i in this.entities)
        {
            if(this.entities[i].px==x && this.entities[i].py==y && this.entities[i].type != toIgnore) return this.entities[i];
        }
        return undefined;
	},
	
	getEntityOfTypeAt: function(x, y, type)
	{
		for(i in this.entities)
        {
            if(this.entities[i].px==x && this.entities[i].py==y && this.entities[i].type==type) return this.entities[i];
        }
        return undefined;
	},
	
	getEntity: function(id)
	{
		for(i in this.entities)
        {
            if(this.entities[i].id==id) return this.entities[i];
        }
        return undefined;
	},
	
	nextIndexOfType :function(type)
	{
		count = 0;
		while(true)
		{
			foundIndex = false;
			for(i in this.entities)
	        {
	            if(this.entities[i].type==type && this.entities[i].id.endsWith(""+count)) foundIndex=true;
	        }
	        if(!foundIndex) return count;
	        count++;
		}
		
		/*for(i in this.entities)
        {
            if(this.entities[i].type==type) count++;
        }*/
        return count;
	},
	
	getEvents: function()
	{
		if(grid.paused) return;
		//Request Events
        setTimeout(function(){
        	if(grid.serverInitialised)
        	{
		        Golem.getEvents( function(responseText){
	                //parse all the events and add each event to the queue of the corresponding entity
	                events = responseText.split("\n");
					for(i in events)
			        {
			        	event = events[i];
				        ec = event.split(",");
			        	entity = grid.getEntity(ec[1]);
			        	if(entity!=undefined) entity.addEvent(ec);
			        }
	            } );
		    }
        	grid.getEvents();
        }, getEventsEvery);
	},
	
	//elementType should be an entry in the types data structure
	addEntity: function(entityId, entityName, entityType, px, py, orientation)
	{
		if(orientation == undefined) orientation = 0;
		entity = new GridEntity(this, entityId, entityName, entityType, px, py, orientation);
		this.entities.push(entity);
		
		//if the server hasn't been initialised, the entities positions will be sent in the init url
		if(this.serverInitialised)
		{
			Golem.addEntity(entityType.typeName, "id="+entityId+"&x="+px+"&y="+py, 
				function(responseText){
				}
			);
		}
		
		return entity;
	},
	
	removeEntity: function(entity){
		if(entity.type==window.entityTypes.dirt) Golem.removeDirt(entity.px, entity.py);
		this.entities = removeFromArray(entity, this.entities);
		//remove tab, tab control, and graphic
		$('div[entityid="' + entity.id + '"]').remove(); //graphic
		$('div[id="' + entity.id + '-tab"]').remove(); //tab
		$('li[entityid="' + entity.id + '"]').remove(); //menu
	},
	
	terminate: function(){
		grid.pause();
		Golem.terminate(function(responseText){
			//alert("terminated");
		});
    	$('#execution-status').html("terminated");
	}
}

function setupGUI()
{
	
	//remove all the menus for pervious parameters
	$('li[entityid]').remove();
	$('div[entitytab]').remove();
	
	//Environment parameters
	//add parameters if there aren't any
	if(!guiInitialised)
	{
		form = $("<form></form>").appendTo("#environment-parameters");
		$("<h4>Create Grid</h4>").appendTo(form);
		$('<p>width: <input type="text" value="20" id="grid-width"></p>').appendTo(form);
		$('<p>height: <input type="text" value="20" id="grid-height"></p>').appendTo(form);
		$('<p><input type="button" value="create" id="create-grid-button"></p>').appendTo(form);
		
		$("#create-grid-button").click(function() {
			//pause the previous grid to kill loops
			grid.terminate();
			width = parseInt($("#grid-width").val(), 10);
			height = parseInt($("#grid-height").val(), 10);
			grid = new Grid("canvas-container", width, height);
			grid.initServer();
		});
		
		$("<h4>Execution (<span id='execution-status'></span>)</h4>").appendTo(form);
		pauseButton = $('<input type="button" value="pause" id="pause-button">').appendTo(form);
		$(pauseButton).click(function() {
			grid.pause();
		});
		resumeButton = $('<input type="button" value="resume" id="resume-button">').appendTo(form);
		$(resumeButton).click(function() {
			grid.resume();
		});
		terminateButton = $('<input type="button" value="terminate" id="terminate-button">').appendTo(form);
		$(terminateButton).click(function() {
			grid.terminate();
		});
		
		//User Avatar GUI
		form = $("<form></form>").appendTo("#user-parameters");
		$('<p><input type="button" value="clean" id="clean-button"></p>').appendTo(form);
		$("#clean-button").click(function() {
			grid.user.clean();
		});
		
		$('<p><input type="button" value="drop dirt" id="dirt-button"></p>').appendTo(form);
		$("#dirt-button").click(function() {
			nDirts = grid.nextIndexOfType(window.entityTypes.dirt);
			grid.addEntity("dirt"+nDirts, "Dirt " + nDirts, window.entityTypes.dirt, grid.user.px, grid.user.py, 0);
		});
		
		guiInitialised = true;
	}
	
	//Create New Agent Button
	newAgent = $('<li entityid=""><div> + New</div></li>').appendTo('#agents-tab-controls');
	$(newAgent).click(function() {
		//look for an empty place to place the agent
		for(x = 0; x < grid.width; x++)
		{
			for(y = 0; y < grid.height; y++)
			{
				if(grid.getEntityAt(x, y)==undefined)
				{
					nAgents = grid.nextIndexOfType(window.entityTypes.agent);
					grid.addEntity("ag"+nAgents, "Agent " + nAgents, window.entityTypes.agent, x, y, 0);
					return;
				}
			}
		}
	});
	
	//Create New Dirt Button
	newDirt = $('<li entityid=""><div> + New</div></li>').appendTo('#objects-tab-controls');
	$(newDirt).click(function() {
		//look for an empty place to place the agent
		for(x = 0; x < grid.width; x++)
		{
			for(y = 0; y < grid.height; y++)
			{
				if(grid.getEntityAt(x, y)==undefined)
				{
					nDirts = grid.nextIndexOfType(window.entityTypes.dirt);
					grid.addEntity("dirt"+nDirts, "Dirt " + nDirts, entityTypes.dirt, x, y, 0);
					return;
				}
			}
		}
	});
}

//setupGUI();

///////// TEMP GRID INIT ////////
$( document ).ready(function() {
	grid = new Grid("canvas-container", 20, 20);
	grid.addEntity("ag0", "Agent 0", window.entityTypes.agent, 0, 6, 0);
	grid.addEntity("ag1", "Agent 1", window.entityTypes.agent, 5, 7, 0);
	grid.addEntity("dirt0", "Dirt 0", window.entityTypes.dirt, 0, 1);
	grid.addEntity("dirt1", "Dirt 1", window.entityTypes.dirt, 12, 3);
	grid.addEntity("dirt2", "Dirt 2", window.entityTypes.dirt, 18, 2);
	grid.addEntity("dirt3", "Dirt 3", window.entityTypes.dirt, 10, 16);
	grid.initServer();
});



//terminate server on window close
$( window ).unload(function() {
	grid.terminate();
});
	        



//////// TODO ///////
// Better layout, table


// use real event times scale the timestamp (but this is fine for now)
// select type for new object
// check the left column on firefox -> probably change the layout to table, also solves the extra height
// 
// Receive and upload agent code
//     An agent parameters should show the agent code and allow it to be changed (click and show code in lightbox)
// Upload agent code and give it a name. Then when creating an agent, people can select agent type
