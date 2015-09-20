$(document).on('click', '.tab-control', tabControlClick);


function tabControlClick()
{
	tab = $(this).attr("tab");
	$(".tab").removeClass("active-tab");
	$("#"+tab).addClass("active-tab");
	$(".tab-control").removeClass("active-tab-control");
	$(this).addClass("active-tab-control");
	$('*[entityid]').removeClass("selected-entity");
	$('*[entityid="' + $(this).attr("entityid") + '"]').addClass("selected-entity");
}

//kind is either "agent" or "dirt"
function addTab(kind, entityid, entityName)
{
	//add tab control
	$('<li class="tab-control" tab="' + entityid + '-tab" entityid="' + entityid + '"><div>' + entityName + '</div></li>').appendTo('#' + kind + 's-tab-controls');
	
	//add tab
	tab = $('<div class="tab" id="' + entityid + '-tab" entitytab="true"><div>')
	$(tab).appendTo('#tabs');
	
	return tab[0];
}


/***** DEBUG ****/
function log(obj)
{
	console.log(obj);
}