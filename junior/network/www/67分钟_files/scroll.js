if(location.hash.indexOf("scroll")>=0)(function(){
	var position = 0,timer;
	var scroller=function() {
		position+=2;
		scroll(0,position);
		clearTimeout(timer);
		timer=setTimeout(scroller,50);
	}
	scroller();
})();
