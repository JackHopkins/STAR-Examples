debug = false;


/////////////////////////////// Debug ///////////////////////////////

function log(msg) {
    if(!debug) return;
    if(typeof msg === 'object')
    {
        console.log(msg);
    }
    else
    {
        console.log("inform: " + msg);
    }
}

function error(msg) {
    if(!debug) return;
    console.error("inform: " + msg);
}

//////////////////////// Time ////////////////////////////////

// converts a string in the format mm:ss to the number of seconds
function timeStringToSeconds(time)
{
	if(time.split==undefined) return 0;
	tmp = time.split(":");
	if(tmp==undefined) return 0;
	min = parseFloat(tmp[0]);
	if(isNaN(min)) min=0;
	if(tmp.length<2) return min; //if there isn't a ":"
	sec = parseFloat(tmp[1]);
	if(isNaN(sec)) sec=0;
	return min*60 + sec;
}

function secondsToTimeString(seconds)
{
	s = seconds%60;
	if(s<10) s = "0" + s;
	m = Math.floor((seconds - (seconds%60))/60);
	if(m<10) m = "0" + m;
	return m + ":" + s;
}

//////////////////////// Array ////////////////////////////////

//returns a new array without the element in parameter
function removeFromArray(element, array)
{
	newArr = [];
	for(i in array)
	{
		if(array[i]!=element) newArr.push(array[i]);
	}
	return newArr;
}

//////////////////////// String ////////////////////////////////

String.prototype.endsWith = function(suffix) {
    return this.indexOf(suffix, this.length - suffix.length) !== -1;
};