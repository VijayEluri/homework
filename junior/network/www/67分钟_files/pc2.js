/**
 * Author : zhanhb
 * QQ     : 815703271
 * Build  : 20121031133535
 */
(function(w,$,hasSchoolRank,boardFrozen,contestEnd) {
	var schools={
"*Google?Optimus?Prime":{"name":"\u0047\u006f\u006f\u0067\u006c\u0065","star":""},
"Tesseract":{"name":"\u5b89\u5fbd\u5927\u5b66"},
"*Team_17":{"name":"\u5317\u4eac\u5927\u5b66","star":""},
"htc_buaa":{"name":"\u5317\u4eac\u822a\u7a7a\u822a\u5929\u5927\u5b66"},
"welikeballoons_buaa":{"name":"\u5317\u4eac\u822a\u7a7a\u822a\u5929\u5927\u5b66"},
"BJTU_RETURN":{"name":"\u5317\u4eac\u4ea4\u901a\u5927\u5b66"},
"SecondChance":{"name":"\u5317\u4eac\u4ea4\u901a\u5927\u5b66"},
"BIT_TaZhaLe":{"name":"\u5317\u4eac\u7406\u5de5\u5927\u5b66"},
"BIT-BlackStar":{"name":"\u5317\u4eac\u7406\u5de5\u5927\u5b66"},
"bfwstyle":{"name":"\u5317\u4eac\u6797\u4e1a\u5927\u5b66"},
"Sellamoe":{"name":"\u5317\u4eac\u5e08\u8303\u5927\u5b66"},
"BNUZH_Waitus":{"name":"\u5317\u4eac\u5e08\u8303\u5927\u5b66\u73e0\u6d77\u5206\u6821"},
"a^=b^=a^=b":{"name":"\u5317\u4eac\u4fe1\u606f\u79d1\u6280\u5927\u5b66"},
"Actinium":{"name":"\u5317\u4eac\u90ae\u7535\u5927\u5b66"},
"Secret?base":{"name":"\u5317\u4eac\u90ae\u7535\u5927\u5b66"},
"CHD":{"name":"\u957f\u5b89\u5927\u5b66"},
"Pandaria":{"name":"\u91cd\u5e86\u5927\u5b66"},
"Explorer":{"name":"\u6210\u90fd\u4e1c\u8f6f"},
"rice":{"name":"\u6210\u90fd\u4e1c\u8f6f"},
"Flying_Stones":{"name":"\u5927\u8fde\u7406\u5de5\u5927\u5b66"},
"Unlimited":{"name":"\u5927\u8fde\u7406\u5de5\u5927\u5b66"},
"UESTC-Cumian":{"name":"\u7535\u5b50\u79d1\u6280\u5927\u5b66"},
"UESTC-Rotelle":{"name":"\u7535\u5b50\u79d1\u6280\u5927\u5b66"},
"zero?break":{"name":"\u4e1c\u5317\u6797\u4e1a\u5927\u5b66"},
"brilliance":{"name":"\u4e1c\u5317\u5e08\u8303\u5927\u5b66"},
"HEHE":{"name":"\u4e1c\u5317\u5e08\u8303\u5927\u5b66"},
"Shuxiao":{"name":"\u4e1c\u5317\u5e08\u8303\u5927\u5b66"},
"TRY":{"name":"\u4e1c\u534e\u5927\u5b66"},
"Lab107":{"name":"\u798f\u5efa\u5e08\u8303\u5927\u5b66"},
"Lunatic":{"name":"\u798f\u5efa\u5e08\u8303\u5927\u5b66"},
"FZU_HappyLatte":{"name":"\u798f\u5dde\u5927\u5b66"},
"FZU_Struggler":{"name":"\u798f\u5dde\u5927\u5b66"},
"FZU_WhyWhy":{"name":"\u798f\u5dde\u5927\u5b66"},
"bulala":{"name":"\u590d\u65e6\u5927\u5b66"},
"LeGenD.N":{"name":"\u590d\u65e6\u5927\u5b66"},
"mobaizyb":{"name":"\u590d\u65e6\u5927\u5b66"},
"Word_Final":{"name":"\u5e7f\u4e1c\u5de5\u4e1a\u5927\u5b66"},
"ACOnFingers":{"name":"\u56fd\u9632\u79d1\u5b66\u6280\u672f\u5927\u5b66"},
"ALCP_MAGI":{"name":"\u56fd\u9632\u79d1\u5b66\u6280\u672f\u5927\u5b66"},
"FruitNinja?@HRBEU":{"name":"\u54c8\u5c14\u6ee8\u5de5\u7a0b\u5927\u5b66"},
"Lupus?@HRBEU":{"name":"\u54c8\u5c14\u6ee8\u5de5\u7a0b\u5927\u5b66"},
"HIT_cherry":{"name":"\u54c8\u5c14\u6ee8\u5de5\u4e1a\u5927\u5b66"},
"HIT_HIworld":{"name":"\u54c8\u5c14\u6ee8\u5de5\u4e1a\u5927\u5b66"},
"HIT07":{"name":"\u54c8\u5c14\u6ee8\u5de5\u4e1a\u5927\u5b66"},
"Dragon?Fight":{"name":"\u54c8\u5c14\u6ee8\u7406\u5de5\u5927\u5b66"},
"HDU-ACHunter":{"name":"\u676d\u5dde\u7535\u5b50\u79d1\u6280\u5927\u5b66"},
"HDU-Alaligehua":{"name":"\u676d\u5dde\u7535\u5b50\u79d1\u6280\u5927\u5b66"},
"HDU-Finalchance":{"name":"\u676d\u5dde\u7535\u5b50\u79d1\u6280\u5927\u5b66"},
"*simplicissimus":{"name":"\u676d\u5dde\u5916\u56fd\u8bed\u5b66\u6821","star":""},
"*HZXJHS1":{"name":"\u676d\u5dde\u5b66\u519b\u4e2d\u5b66","star":""},
"*HZXJHS2":{"name":"\u676d\u5dde\u5b66\u519b\u4e2d\u5b66\u003f","star":""},
"HFUT_Nexus":{"name":"\u5408\u80a5\u5de5\u4e1a\u5927\u5b66"},
"H,but...Crazy":{"name":"\u6e56\u5317\u5de5\u4e1a\u5927\u5b66"},
"hbmy_fighting":{"name":"\u6e56\u5317\u6c11\u65cf\u5b66\u9662"},
"Acgege":{"name":"\u6e56\u5357\u5927\u5b66"},
"**?Coconuts":{"name":"\u6e56\u5357\u5927\u5b66"},
"Simpler":{"name":"\u6e56\u5357\u5927\u5b66"},
"JKL":{"name":"\u6e56\u5357\u5e08\u8303\u5927\u5b66"},
"GobletofFire":{"name":"\u534e\u5317\u7535\u529b\u5927\u5b66"},
"Flyhw~~fighting":{"name":"\u534e\u4e1c\u4ea4\u901a\u5927\u5b66"},
"PrimeShark":{"name":"\u534e\u4e1c\u4ea4\u901a\u5927\u5b66"},
"c3s":{"name":"\u534e\u4e1c\u7406\u5de5\u5927\u5b66"},
"eternalLight":{"name":"\u534e\u4e1c\u5e08\u8303\u5927\u5b66"},
"*?OnePointFive":{"name":"\u534e\u4e1c\u5e08\u8303\u5927\u5b66","star":""},
"SCUT_Yes_iwantowin":{"name":"\u534e\u5357\u7406\u5de5\u5927\u5b66"},
"SCUT_Yes_Nova":{"name":"\u534e\u5357\u7406\u5de5\u5927\u5b66"},
"Scau_Horrific_Snoopy":{"name":"\u534e\u5357\u519c\u4e1a\u5927\u5b66"},
"Night_Elf":{"name":"\u534e\u5357\u5e08\u8303\u5927\u5b66"},
"*?Galaxy":{"name":"\u534e\u5e08\u65c5\u6e38\u961f","star":""},
"ErBao":{"name":"\u534e\u4e2d\u79d1\u6280\u5927\u5b66"},
"hehehe":{"name":"\u534e\u4e2d\u79d1\u6280\u5927\u5b66"},
"Bazinga!":{"name":"\u534e\u4e2d\u5e08\u8303\u5927\u5b66"},
"Inspiration":{"name":"\u5409\u6797\u5927\u5b66"},
"Meditation":{"name":"\u5409\u6797\u5927\u5b66"},
"tomorrow":{"name":"\u5409\u6797\u901a\u5316\u5e08\u8303\u5b66\u9662"},
"polar?light":{"name":"\u6c5f\u82cf\u5927\u5b66"},
"*?WJZB":{"name":"\u6c5f\u82cf\u7701\u5e38\u5dde\u9ad8\u7ea7\u4e2d\u5b66","star":""},
"jxust_leap":{"name":"\u6c5f\u897f\u7406\u5de5\u5927\u5b66"},
"TeamUp@USC":{"name":"\u5357\u534e\u5927\u5b66"},
"NJU_Daisy_Major":{"name":"\u5357\u4eac\u5927\u5b66"},
"ACE_Polaris":{"name":"\u5357\u4eac\u822a\u7a7a\u822a\u5929\u5927\u5b66"},
"ACmoment":{"name":"\u5357\u4eac\u7406\u5de5\u5927\u5b66"},
"Coolness":{"name":"\u5357\u4eac\u7406\u5de5\u5927\u5b66"},
"WonderfloW":{"name":"\u5357\u4eac\u7406\u5de5\u5927\u5b66"},
"Accela":{"name":"\u5357\u4eac\u90ae\u7535\u5927\u5b66"},
"E-V-O-L":{"name":"\u5357\u4eac\u90ae\u7535\u5927\u5b66"},
"NKU-翘课来比赛":{"name":"\u5357\u5f00\u5927\u5b66"},
"RAH":{"name":"\u5357\u9633\u7406\u5de5\u5b66\u9662"},
"CXY_UBN":{"name":"\u5b81\u6ce2\u5927\u5b66"},
"DM(){?DD?!=?NE?}":{"name":"\u5b81\u6ce2\u5de5\u7a0b\u5b66\u9662"},
"*?Again?And?Again":{"name":"\u6e05\u534e\u5927\u5b66","star":""},
"DivineRapier":{"name":"\u6e05\u534e\u5927\u5b66"},
"HOLD":{"name":"\u6e05\u534e\u5927\u5b66"},
"QuicklyDone":{"name":"\u6e05\u534e\u5927\u5b66"},
"Big?Boss":{"name":"\u5c71\u4e1c\u5927\u5b66"},
"Flood":{"name":"\u5c71\u4e1c\u7406\u5de5\u5927\u5b66"},
"SdauTeam1":{"name":"\u5c71\u4e1c\u519c\u4e1a\u5927\u5b66"},
"SHU_RAIN":{"name":"\u4e0a\u6d77\u5927\u5b66"},
"**Blooooom":{"name":"\u4e0a\u6d77\u4ea4\u901a\u5927\u5b66"},
"ColorlessWind":{"name":"\u4e0a\u6d77\u4ea4\u901a\u5927\u5b66"},
"*Mithri1":{"name":"\u4e0a\u6d77\u4ea4\u901a\u5927\u5b66","star":""},
"*MithriI":{"name":"\u4e0a\u6d77\u4ea4\u901a\u5927\u5b66","star":""},
"SiGkILL":{"name":"\u4e0a\u6d77\u4ea4\u901a\u5927\u5b66"},
"SUT_Snow":{"name":"\u6c88\u9633\u5de5\u4e1a\u5927\u5b66"},
"TJU_ToT":{"name":"\u5929\u6d25\u5927\u5b66"},
"*?TJU_Virus":{"name":"\u5929\u6d25\u5927\u5b66","star":""},
"TJU_What_a_water":{"name":"\u5929\u6d25\u5927\u5b66"},
"*CounterAttack":{"name":"\u540c\u6d4e\u5927\u5b66","star":""},
"WHU_openimage":{"name":"\u6b66\u6c49\u5927\u5b66"},
"WHU_OpenSonata":{"name":"\u6b66\u6c49\u5927\u5b66"},
"TNT":{"name":"\u6b66\u6c49\u79d1\u6280\u5927\u5b66"},
"XDU_Truth":{"name":"\u897f\u5b89\u7535\u5b50\u79d1\u6280\u5927\u5b66"},
"xdu_zero":{"name":"\u897f\u5b89\u7535\u5b50\u79d1\u6280\u5927\u5b66"},
"Apple":{"name":"\u897f\u5b89\u4ea4\u901a\u5927\u5b66"},
"geeks":{"name":"\u897f\u5317\u5de5\u4e1a\u5927\u5b66"},
"SWUST_Plover":{"name":"\u897f\u5357\u79d1\u6280\u5927\u5b66"},
"look?big?cow":{"name":"\u897f\u5357\u6c11\u65cf\u5927\u5b66"},
"无敌小白菜":{"name":"\u897f\u5357\u6c11\u65cf\u5927\u5b66"},
"Good?evening":{"name":"\u897f\u5357\u77f3\u6cb9\u5927\u5b66"},
"XMU_bobo2":{"name":"\u53a6\u95e8\u5927\u5b66"},
"AcCelebration":{"name":"\u9999\u6e2f\u4e2d\u6587\u5927\u5b66"},
"prime_melody":{"name":"\u6e58\u6f6d\u5927\u5b66"},
"WorldTraveller":{"name":"\u6e58\u6f6d\u5927\u5b66"},
"acmer's?dream":{"name":"\u6d59\u6c5f\u8d22\u7ecf\u5b66\u9662"},
"bluesky":{"name":"\u6d59\u6c5f\u8d22\u7ecf\u5b66\u9662"},
"laugh?out?loud":{"name":"\u6d59\u6c5f\u8d22\u7ecf\u5b66\u9662"},
"Back?On?My?Feet":{"name":"\u6d59\u6c5f\u4f20\u5a92\u5b66\u9662"},
"ACiX":{"name":"\u6d59\u6c5f\u5927\u5b66"},
"*ArcadiaConvent":{"name":"\u6d59\u6c5f\u5927\u5b66","star":""},
"FinalStrike":{"name":"\u6d59\u6c5f\u5927\u5b66"},
"way?to?answer":{"name":"\u6d59\u6c5f\u5927\u5b66"},
"Bleeding":{"name":"\u6d59\u6c5f\u5927\u5b66\u57ce\u5e02\u5b66\u9662"},
"FBH":{"name":"\u6d59\u6c5f\u5927\u5b66\u5b81\u6ce2\u7406\u5de5\u5b66\u9662"},
"POAD":{"name":"\u6d59\u6c5f\u5927\u5b66\u5b81\u6ce2\u7406\u5de5\u5b66\u9662"},
"ZJUT-AC_4_Boxed_Meal":{"name":"\u6d59\u6c5f\u5de5\u4e1a\u5927\u5b66"},
"ZJUT-Philharmonic":{"name":"\u6d59\u6c5f\u5de5\u4e1a\u5927\u5b66"},
"ZJUT-Turbine":{"name":"\u6d59\u6c5f\u5de5\u4e1a\u5927\u5b66"},
"*?FivePenUnderground":{"name":"\u6d59\u6c5f\u7406\u5de5\u5927\u5b66","star":""},
"Sun?shine":{"name":"\u6d59\u6c5f\u519c\u6797\u5927\u5b66"},
"accinfig":{"name":"\u6d59\u6c5f\u5e08\u8303\u5927\u5b66"},
"Esp?Thunder":{"name":"\u6d59\u6c5f\u5e08\u8303\u5927\u5b66"},
"hit?altman":{"name":"\u6d59\u6c5f\u5e08\u8303\u5927\u5b66"},
"The?Beauty?of?Thinking":{"name":"\u6d59\u6c5f\u5e08\u8303\u5927\u5b66\u884c\u77e5\u5b66\u9662"},
"ZCMU-Breeze-forever":{"name":"\u6d59\u6c5f\u4e2d\u533b\u836f\u5927\u5b66"},
"zzuliFrank":{"name":"\u90d1\u5dde\u8f7b\u5de5\u4e1a\u5b66\u9662"},
"Sunny":{"name":"\u4e2d\u56fd\u5730\u8d28\u5927\u5b66\uff08\u5317\u4eac\uff09"},
"DreamCoder":{"name":"\u4e2d\u56fd\u5730\u8d28\u5927\u5b66\uff08\u6b66\u6c49\uff09"},
"Glegooder":{"name":"\u4e2d\u56fd\u79d1\u5b66\u6280\u672f\u5927\u5b66"},
"KingBase_CJ":{"name":"\u4e2d\u56fd\u4eba\u6c11\u5927\u5b66"},
"KingBase_WYW":{"name":"\u4e2d\u56fd\u4eba\u6c11\u5927\u5b66"},
"GoldenSand":{"name":"\u4e2d\u56fd\u77f3\u6cb9\u5927\u5b66"},
"CSU_BMW":{"name":"\u4e2d\u5357\u5927\u5b66"},
"DawnOfWish":{"name":"\u4e2d\u5357\u6c11\u65cf\u5927\u5b66"},
"*SYSU_BlackBlade":{"name":"\u4e2d\u5c71\u5927\u5b66","star":""},
"SYSU_LankyWolf":{"name":"\u4e2d\u5c71\u5927\u5b66"},
"SYSU_PhantomThief":{"name":"\u4e2d\u5c71\u5927\u5b66"},
"LtRoicePhantom":{"name":"\u8fbd\u5b81\u4e2d\u533b\u836f\u5927\u5b66"}
};
	var title="\u7b2c37\u5c4aACM\u56fd\u9645\u5927\u5b66\u751f\u7a0b\u5e8f\u8bbe\u8ba1\u7ade\u8d5b\u4e9a\u6d32\u533a\u57df\u8d5b\u676D\u5DDE\u7ad9";
	var doc = w.document;
	var table = $("table:first"), tr = table.find("tr");
	var ac = /^\d+\/(\d+)$/, fail = /^(\d+)\/--$/;
	var RANK=0,NAME=1;
	//var a = q("<a>Simple Version</a>").attr("href", "?simple");

	var array=[],nameToRow={},emptyRow=[],lastcolumn=0,headRows=[];
	var Set=function(){
		this._map={};
		this.add=function(a){this._map[a]="";}
		this.contains=function(a){return typeof this._map[a]!="undefined";}
	}
	var Row=function(a,b,c){
		$.isArray(a)?this.init(a[1],a[2],a[3]):this.init(a,b,c);
	}
	Row.prototype.init=function(a,b,c){
		this.name=a;
		this.solved=parseInt(b);
		this.time=parseInt(c);
	}
	Row.prototype.isStar=function(){
		var sch = schools[this.name];
		return sch && typeof sch["star"]!="undefined";
	}
	Row.prototype.compareTo=function(a){
		if(!a)throw "can't compare to " + (a===null?"null":typeof a);
		if(this.solved!=a.solved)return this.solved<a.solved?-1:1;
		if(this.time!=a.time)return this.time<a.time?-1:1;
		return 0;
	}
	Row.prototype.equals=function(a){
		try{return a&&this.compareTo(a)==0;}catch(e){return false;}
	}
	Row.prototype.each=function(){
		var a=nameToRow[this.name];
		return $.isFunction(a&&a.each)?a.each.apply(a,arguments):a;
	}
	tr.each(function(){
		var tds=$("td",this);
		if(tds.size()==0){
			headRows.push($("th",this));
		}else{
			var arr=[];
			tds.each(function(cols){
				if(lastcolumn<cols)lastcolumn=cols;
				arr.push($(this).text());
			});
			if(!isNaN(parseInt(arr[RANK]))){
				array.push(arr);
				var name=arr[NAME];
				nameToRow[name]=tds;
			}else{
				emptyRow.push(tds);
			}
		}
	});

	var gRank=0,gSchoolRank=0,minTime={},schoolSet=new Set();
	var template=function(doTeam,doStar){
		$(array).each(function(){
			var now=new Row(this);
			if(now.isStar()){
				if(typeof doStar=="function")doStar(now);
			} else {
				if(typeof doTeam=="function")doTeam(now);
			}
		});
	}

	var lastTeamSet,lastSchoolSet;
	template(function(now){
		++gRank;
		if(now.equals(lastTeamSet)){
			now.rank=lastTeamSet.rank;
		}else{
			now.rank=gRank;
		}
		lastTeamSet=now;
		if(hasSchoolRank){
			var school=schools[now.name];
			if(school){
				var schoolName=school.name;
				if(!schoolSet.contains(schoolName)){
					++gSchoolRank;
					if(now.equals(lastSchoolSet)){
						now.schoolRank=lastSchoolSet.schoolRank;
					}else{
						now.schoolRank=gSchoolRank;
					}
					schoolSet.add(schoolName);
					lastSchoolSet=now;
				}else{
					now.schoolRank="";
				}
				now.schoolName=schoolName;
			} else {
				console.log("\u8fd9\u652f\u961f\u4f0d\u54ea\u91cc\u6765\u7684\uff0c\u600e\u4e48\u6ca1\u6709\u5b66\u6821?Team name: "+now.name);
				now.schoolName="?";
				now.schoolRank="?";
			}
		}
		//console.log(now.rank+" "+now.schoolName+" "+now.schoolRank);
		now.each(function(column) {
			var I=$(this);
			if(column==RANK){
				I.text(now.rank);
			}else if(column==NAME){
				if(hasSchoolRank){
					I.after("<td>"+now.schoolRank+"</td>").after("<td>"+now.schoolName+"</td>");
				}
			}
		})
	},function(now){
		now.each(function(column) {
			var I=$(this),text=I.text();
			if(column==RANK){
				I.text("*");
			}else if(column==NAME){
				if(hasSchoolRank){
					var a=schools[text];
					I.after($("<td>")).after($("<td>").text(a && a["name"] || ""));
				}
			}
		})
	});

	template(function(now){
		now.each(function(column) {
			var I=$(this),text=I.text();
			if(ac.test(text)) {
				var tmp=parseInt(RegExp.$1);
				if(typeof minTime[column]=="undefined" || minTime[column]>tmp)
					minTime[column]=tmp;
			}
			if(fail.test(text) && parseInt(RegExp.$1)) // if the text matches ??/-- then fail
				I.addClass("fail");
		})
	});
	template(function(now){
		now.each(function(column) {
			var I=$(this),text=I.text();
			if(ac.test(text) && column != lastcolumn) { // the last column is the total score
				I.addClass(minTime[column] == RegExp.$1 ? "firstac" : "ac"); // judge if first one to ac
			}
		})
	},function(now){
		now.each(function(){
			return false;
			var I=q(this),text=I.text();
			if(ac.test(text))I.addClass("ac");
			else if(fail.test(text)&&parseInt(RegExp.$1))I.addClass("fail");
		});
	});
	if(hasSchoolRank){
		$(emptyRow).each(function(){
			this.each(function(column) {
				if(column==NAME){
					$(this).after($("<td>")).after($("<td>"));
				}
			});
		})
		$(headRows).each(function(){
			this.each(function(column) {
				if(column==NAME){
					$(this).after("<th><strong><u>School Rank</strong></u></th>").after("<th><strong><u>School</strong></u></th>");
				}
			})
		})
	}

	table.attr("id", "standings");
	$("p:last").remove();
	doc.title = title;
	table.before("<h1 align='center'>"+title+"</h1>");
	if(boardFrozen){
		var marquee=$("<marquee scrollamount=\"2\" width=\"100%\" scrolldelay=\"30\" onmouseover=\"javascript:this.stop();\" onmouseout=\"javascript:this.start();\"></marquee>");
		marquee.html("<font style='color: red'>Board is frozen.</font>");
		table.before(marquee);
	}
	var refresh = !contestEnd && ! boardFrozen;
	if(refresh){
		setTimeout("window.location.reload()",110000);
	}
})(window,jQuery,true,false,false);

