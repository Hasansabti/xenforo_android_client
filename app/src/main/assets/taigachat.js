/* Darkimmortal's TaigaChat */

var taigachat_initialFired = false;
var taigachat_focused = true;
var taigachat_reverse = false;
var taigachat_initialTime = 0;
var taigachat_lastRefresh = 0;
var taigachat_lastRefreshServer = 0;
var taigachat_lastMessage = 0;
var taigachat_nextRefresh = 0;
var taigachat_isRefreshing = false;
var taigachat_tabUnfocused = false;
var taigachat_lastScroll = 0;
var taigachat_lastPostTime = 0;
var taigachat_lastPostMessage = "";
var taigachat_customColor = "";
var taigachat_scrolled = false;
var taigachat_boxHeight = 0;
var taigachat_hidden, taigachat_visibilityChange;


XenForo.taigachat_PopupMenu = XenForo.PopupMenu;
XenForo.taigachat_PopupMenu.setMenuPosition = function(caller){
	//console.info('setMenuPosition(%s)', caller);

	var controlLayout, // control coordinates
		menuLayout, // menu coordinates
		contentLayout, // #content coordinates
		$content,
		$window,
		proposedLeft,
		proposedTop;

	controlLayout = this.$control.coords('outer');

	this.$control.removeClass('BottomControl');

	// set the menu to sit flush with the left of the control, immediately below it
	this.$menu.removeClass('BottomControl').css(
	{
		left: controlLayout.left,
		top: controlLayout.top + controlLayout.height
	});

	menuLayout = this.$menu.coords('outer');

	$content = $('#content .pageContent');
	if ($content.length)
	{
		contentLayout = $content.coords('outer');
	}
	else
	{
		contentLayout = $('body').coords('outer');
	}

	$window = $(window);
	$window.sT = $window.scrollTop();
	$window.sL = $window.scrollLeft();

	/*
	 * if the menu's right edge is off the screen, check to see if
	 * it would be better to position it flush with the right edge of the control
	 */
	if (menuLayout.left + menuLayout.width > contentLayout.left + contentLayout.width)
	{
		proposedLeft = controlLayout.left + controlLayout.width - menuLayout.width;
		// must always position to left with mobile webkit as the menu seems to close if it goes off the screen
		if (proposedLeft > $window.sL || XenForo._isWebkitMobile)
		{
			this.$menu.css('left', proposedLeft);
		}
	}

	/*
	 * if the menu's bottom edge is off the screen, check to see if
	 * it would be better to position it above the control
	 */
	//if (menuLayout.top + menuLayout.height > $window.height() + $window.sT)
	{
		proposedTop = controlLayout.top - menuLayout.height-500;
		//if (proposedTop > $window.sT)
		{
			this.$control.addClass('BottomControl');
			this.$menu.addClass('BottomControl');
			this.$menu.css('top', proposedTop);
		}
	}
};

function fixBalloonCounter(){
	// workaround lack of support for doing nothing to alert counts
	XenForo.balloonCounterUpdate = function($balloon, newTotal)
	{
		if(newTotal == 'IGNORE')
			return;

		if ($balloon.length)
		{
			var $counter = $balloon.find('span.Total'),
				oldTotal = $counter.text();

			$counter.text(newTotal);

			if (!newTotal || newTotal == '0')
			{
				$balloon.fadeOut('fast');
			}
			else
			{
				$balloon.fadeIn('fast', function()
				{
					var oldTotalInt = parseInt(oldTotal.replace(/[^\d]/, ''), 10),
						newTotalInt = parseInt(newTotal.replace(/[^\d]/, ''), 10),
						newDifference = newTotalInt - oldTotalInt;

					if (newDifference > 0)
					{
						var PopupMenu = $balloon.closest('.Popup').data('XenForo.PopupMenu'),

						$message = $('<a />').css('cursor', 'pointer').html($balloon.data('text').replace(/%d/, newDifference)).click(function(e)
						{
							PopupMenu.$clicker.trigger('click');
							return false;
						});

						if (!PopupMenu.menuVisible)
						{
							PopupMenu.resetLoader();
						}

						XenForo.stackAlert($message, 10000, $balloon);
					}
				});
			}
		}
	};
}
fixBalloonCounter();

function handleVisibilityChange() {
	taigachat_tabUnfocused = !!document[taigachat_hidden];
}

$(document).ready(function(){

	if(taigachat_room != 1){
		taigachat_speed = false;
	}

	// workaround xenporta recentthreadsx5 bug
	fixBalloonCounter();

	if (typeof document.hidden !== "undefined") {
		taigachat_hidden = "hidden";
		taigachat_visibilityChange = "visibilitychange";
	} else if (typeof document.mozHidden !== "undefined") {
		taigachat_hidden = "mozHidden";
		taigachat_visibilityChange = "mozvisibilitychange";
	} else if (typeof document.msHidden !== "undefined") {
		taigachat_hidden = "msHidden";
		taigachat_visibilityChange = "msvisibilitychange";
	} else if (typeof document.webkitHidden !== "undefined") {
		taigachat_hidden = "webkitHidden";
		taigachat_visibilityChange = "webkitvisibilitychange";
	}

	taigachat_boxHeight = $("#taigachat_box").height();

	if(typeof document.addEventListener !== "undefined" && typeof taigachat_hidden !== "undefined"){
		document.addEventListener(taigachat_visibilityChange, handleVisibilityChange, false);
	}

	$(window).focus(taigachat_focus);

	$(window).blur(function(){
		taigachat_focused = false;
	});

	$(document).mousemove(function(){
		$.flashTitle(false);
	});

	$("#taigachat_message").keypress(function (e) {
		if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
			sendShout();
			return false;
		}
		return true;
	});


	if(document.getElementById('taigachat_full')){
		$(window).resize(function(){
			$("#taigachat_message").css({width:"90%"});
			$("#taigachat_message").width($("#taigachat_message").width() - $("#taigachat_toolbar").width() - 25);
		});
		$("#taigachat_message").width($("#taigachat_message").width() - $("#taigachat_toolbar").width() - 25);
	}


	$("#taigachat_send").click(sendShout);

	$(".taigachat_motd, #taigachat_full .categoryStrip, #taigachat_sidebar h3").dblclick(function(e){
		$("a.OverlayTrigger[href*='taigachat/motd']").trigger('click');
		e.preventDefault();
		e.stopPropagation();
		return false;
	});

	taigachat_lastScroll = new Date().getTime();
	$("#taigachat_box").scroll(function(){
		taigachat_lastScroll = new Date().getTime();
	});

	refreshShoutbox(true, true, false);

	$("#taigachat_send, #taigachat_message").removeAttr('disabled').removeClass('disabled');
	//$("#taigachat_message").val("");

	$("#taigachat_message").focus(function(e){
		if($("#taigachat_toolbar:visible").length == 0){
			$("#taigachat_toolbar").slideDown(500);
		}
	});

	$("#taigachat_controls").after("<div id='taigachat_temp' style='display:none'></div>");

	$(".taigachat_messagetext").live('dblclick', function(e){
		if((taigachat_canModify && $(this).parents("li").data("userid") == XenForo.visitor.user_id) || taigachat_canModifyAll){
			$("#taigachat_edit_"+$(this).parents("li").data("messageid")).click();
		}
		e.preventDefault();
		e.stopPropagation();
		return false;
	});

	$(".taigachat_delete").live('click', function(e){
		e.stopPropagation();
		e.preventDefault();

		var $link = $(this);

		XenForo.ajax(
			$(this).attr("href"),
			{
			},
			function(json){
				$("#taigachat_message_"+$link.data("messageid")).fadeOut(500);
			},
			{
				cache: false
			}
		);

		return false;
	});

	$("#taigachat_smiliepicker").click(function(){
		var self = this;
		var $smilies = $("#taigachat_smilies_box");

		if ($smilies.children().length){
			$smilies.slideToggle();
			return;
		}

		if (self.smiliesPending)
			return;

		self.smiliesPending = true;

		XenForo.ajax(
			'index.php?editor/smilies',
			{},
			function(ajaxData) {
				if (XenForo.hasResponseError(ajaxData)){
					return;
				}

				if (ajaxData.templateHtml){
					$smilies.html(ajaxData.templateHtml);
					$smilies.hide();
					$smilies.on('click', '.Smilie', function(e) {
						e.preventDefault();
						e.stopPropagation();
						if($("#taigachat_message").val() == $("#taigachat_message").attr("placeholder")){
							$("#taigachat_message").removeClass("prompt").val("");
						}
						$("#taigachat_message").insertAroundCaret(" " + $(this).children("img").attr("alt") + " ", "");
						return true;
					});
					$smilies.xfActivate();
					$smilies.slideToggle();
				}
			}
		).complete(function() {
			self.smiliesPending = false;
		});
	});

	$(".taigachat_ban").live('click', function(e){
		XenForo.alert(XenForo.phrases.dark_banned_successfully, '', 2000);
		var href = $(this).data('link');
		setTimeout(function(){
			window.location = href;
		}, 1800);
		e.preventDefault();
		return false;
	});

	$(".taigachat_bbcode").live('click', function(e){

		var bbcode = $(this).attr("data-code");
		if(!bbcode)
			return true;
		e.stopPropagation();
		if($("#taigachat_message").val() == $("#taigachat_message").attr("placeholder")){
			$("#taigachat_message").removeClass("prompt").val("");
		}
		var position = bbcode.length;
		var ins = getCaretLength($("#taigachat_message").get(0)) > 0;
		$("#taigachat_message").insertAroundCaret(bbcode.substring(0, bbcode.indexOf('][')+1), bbcode.substring(bbcode.indexOf('][')+1, bbcode.length));
		if(bbcode.indexOf('=][') != -1){
			position = bbcode.indexOf('=][')+1;
		} else {
			position = bbcode.indexOf('][')+1;
		}

		if(!ins)
			setCaretPosition($("#taigachat_message").get(0), getCaretPosition($("#taigachat_message").get(0)) - (bbcode.length - position));
		else
			setCaretPosition($("#taigachat_message").get(0), getCaretPosition($("#taigachat_message").get(0)) + bbcode.length - position);
		return true;
	});

	if(taigachat_speed && !taigachat_fake){
		setInterval(function(){
			XenForo.ajax(
				taigachat_url_activity,
				{
				},
				function(json){
					if(XenForo.hasResponseError(json) !== false){
						return true;
					}
				},
				{
					cache: false,
					global: false
				}
			);
		}, 145000);
	}

	setTimeout(function(){
		if(XenForo._isWebkitMobile && taigachat_reverse){
			setInterval(function(){
				scrollChatBottom(false);
			}, 100);
		}
	}, 2000);

	if(taigachat_customColor.length == 6)
		$("#taigachat_message").css({color: "#"+taigachat_customColor});

	if($(".taigachat_popup_body, .dark_taigachat_full").length > 0)
		$("#taigachat_message").focus();

	//XenForo.register('.taigachat_Popup', 'XenForo.taigachat_PopupMenu', 'XenForoActivatePopups');
	//XenForo.activate(document);
	/*setTimeout(function(){
		// Add the icon/styling without XenForo registering its own events etc.
		$(".taigachat_Popup").addClass("Popup");
		XenForo.register('.Popup', 'XenForo.PopupMenu', 'XenForoActivatePopups');
		XenForo.activate(document);
	}, 50);*/
});


function sendShout(){

	// silently prevent same message within 5 seconds
	if(taigachat_lastPostTime + 5000 > new Date().getTime() && taigachat_lastPostMessage == $("#taigachat_message").val())
		 return;

	if($("#taigachat_message").val().length == 0 || $("#taigachat_message").val() == $("#taigachat_message").attr("placeholder")) return;
	$("#taigachat_send, #taigachat_message").attr('disabled', true).addClass('disabled');

	taigachat_lastPostMessage = $("#taigachat_message").val();
	taigachat_lastPostTime = new Date().getTime();

	XenForo.ajax(
		taigachat_url_post,
		{
			message: $("#taigachat_message").val(),
			sidebar: taigachat_sidebar ? "1" : "0",
			lastrefresh: taigachat_lastRefreshServer,
			color: taigachat_customColor,
			room: taigachat_room
		},
		function(json){

			if(XenForo.hasResponseError(json) !== false){

				$("#taigachat_send, #taigachat_message").removeAttr('disabled').removeClass('disabled');
				$("#taigachat_message").blur();
				$("#taigachat_message").focus();

				return true;
			}

			var prune = false;

			if(typeof json.too_fast != "undefined" && json.too_fast){
				XenForo.alert(json.phrase, '', 3000);
			} else {

				if($("#taigachat_message").val() == '/prune')
					prune = true;

				if($("#taigachat_message").val().indexOf('/unban') === 0)
					XenForo.alert(XenForo.phrases.dark_unbanned_successfully, '', 3000);

				$("#taigachat_message").val("");

				handleListResponse(json, false, true);

			}

			$("#taigachat_send, #taigachat_message").removeAttr('disabled').removeClass('disabled');
			$("#taigachat_message").blur();
			$("#taigachat_message").focus();

			if(prune)
				location.reload();

		},
		{
			cache: false,
			error: function(xhr, textStatus, errorThrown){
				$("#taigachat_send, #taigachat_message").removeAttr('disabled').removeClass('disabled');
				$("#taigachat_message").blur();
				$("#taigachat_message").focus();
				try
				{
					success.call(null, $.parseJSON(xhr.responseText), textStatus);
				}
				catch (e)
				{
					if(xhr.responseText.substr(0, 1) == '{' && xhr.responseText.substr(-1) == '}')
						XenForo.handleServerError(xhr, textStatus, errorThrown);
				}
			}
		}
	);
}

function taigachat_focus(e){
	taigachat_nextRefresh = 0;
	taigachat_focused = true;
	$.flashTitle(false);
}

// force = ignore focus event delay and ignore document focus
// unsync = out-of-sync request, do not restart timer
function refreshShoutbox(initial, force, unsync){

	// Assert initial refresh will only happen once
	if(initial){
		if(taigachat_initialFired)
			return;
		taigachat_initialFired = true;
		taigachat_initialTime = new Date().getTime();
	} else {
		// Assert we aren't refreshing within 2 seconds of the first refresh - i.e. document focus event
		if(taigachat_initialTime + 2000 > new Date().getTime() && !force)
			return;
	}

	if(taigachat_initialTime + 50 * 60 * 1000 < new Date().getTime() && !initial){
		// time for a CSRF token refresh...
		XenForo._CsrfRefresh.refresh();
		taigachat_refreshtime = 10;
		restartTimer();
		taigachat_initialTime = new Date().getTime();
		return;
	}

	taigachat_isRefreshing = true;

	XenForo.ajax(
		taigachat_speed ? taigachat_speedurl : taigachat_url,
		taigachat_speed ? {} : {
			sidebar: taigachat_sidebar ? "1" : "0",
			lastrefresh: taigachat_lastRefreshServer,
			fake: taigachat_fake ? "1" : "0",
			room: taigachat_room
		},
		function(json, textStatus){
			taigachat_isRefreshing = false;

			if (XenForo.hasResponseError(json))
			{
				return false;
			}

			handleListResponse(json, initial, unsync);

			if(initial){
				setInterval(checkRefresh, 250);
			}
		},
		{
			global: false,
			dataType: 'json',
			cache: false,
			type: taigachat_speed ? 'get' : 'post',
			error: function(xhr, textStatus, errorThrown){
				try
				{
					success.call(null, $.parseJSON(xhr.responseText), textStatus);
				}
				catch (e)
				{
					if(initial){
						setInterval(checkRefresh, 250);
					}

					// workarounds :3
					if(xhr.responseText.substr(0, 1) == '{' && xhr.responseText.substr(-1) == '}')
						XenForo.handleServerError(xhr, textStatus, errorThrown);
				}
				finally
				{
					taigachat_isRefreshing = false;
				}
			}
		}
	); // ajax

	if(!unsync){
		restartTimer();
	}

}

function taigachat_changeColor(){
	var color = $("#color").val();

	XenForo.ajax(
		taigachat_url_savecolor,
		{
			color: color
		},
		function(json){
			if(XenForo.hasResponseError(json) !== false){
				return true;
			}
			taigachat_customColor = json.color;
			$("#taigachat_message").css({color: "#"+json.color});
			XenForo.alert(json.saved, '', 2000);
		},
		{cache: false}
	);
}

function handleListResponse(json, initial, unsync){

	taigachat_lastRefreshServer = parseInt(json.lastrefresh, 10) || 0;

	// error'd
	if(!XenForo.hasTemplateHtml(json) && taigachat_lastRefreshServer == 0){
		XenForo.hasResponseError(json);
		return false;
	}

	// mystery error condition where response contains a xenforo default page
	if(json.templateHtml.indexOf("<html") !== -1)
		return false;

	var gotNew = 0;
	var reverse = parseInt(json.reverse, 10) == 1 ? true : false;
	taigachat_reverse = reverse;

	if(taigachat_lastRefreshServer > taigachat_lastRefresh || unsync){

		taigachat_lastRefresh = taigachat_lastRefreshServer;

		// This is the heavyweight operation
		$template = $(json.templateHtml);

		$("#taigachat_motd").html(json.motd);
		$("#taigachat_count").html(json.numInChat);

		$("#taigachat_online_users_holder").html("");
		$template.filter(".taigachat_list_online_users").children().appendTo("#taigachat_online_users_holder");

		// Grab the chat elements, reverse if not in top to bottom order
		var lis = $template.filter("li").get();
		if(!reverse)
			lis = lis.reverse();

		$(lis).each(function(){
			if(!document.getElementById(this.id)){
				gotNew++;

				var elementToInsert = $(this).attr("style", "visibility:hidden").addClass("taigachat_new");

				if(!reverse){
					// newest message first
					var adjId = $(this).next().length > 0 ? $(this).next().attr("id") : "";
					if(adjId == "" || !document.getElementById(adjId))
						elementToInsert.prependTo("#taigachat_box > ol");
					else
						elementToInsert.insertBefore("#"+adjId);
				} else {
					// newest message last
					var adjId = $(this).prev().length > 0 ? $(this).prev().attr("id") : "";
					if(adjId == "" || !document.getElementById(adjId))
						elementToInsert.appendTo("#taigachat_box > ol");
					else
						elementToInsert.insertAfter("#"+adjId);
				}

			}
		});

		if(initial || gotNew>0){

			if(taigachat_newtab)
				$(".taigachat_new a.internalLink").attr("target", "_blank");

			if(!taigachat_activity_newtab)
				$(".taigachat_new .taigachat_activity a.internalLink").removeAttr("target");
			else
				$(".taigachat_new .taigachat_activity a.internalLink").attr("target", "_blank");


			XenForo.activate($('.taigachat_new'));

			if(!initial && taigachat_showAlert && $(".dark_taigachat_full").length > 0){
				$.flashTitle(XenForo.phrases.dark_new_chat_message, 2000);
			}

			if(taigachat_reverse){
				$("li.taigachat_new img").load(function(e){
					if(initial || $(this).height() > 16)
						scrollChatBottom(true);
				});
			}

			for(var id in XenForo._ignoredUsers){
				$(".taigachat_new[data-userid='"+id+"']").each(function(){
					if(taigachat_ignorehide){
						$(this).addClass("taigachat_ignored").addClass("taigachat_ignorehide")
					} else {
						$(this).addClass("taigachat_ignored").children().removeClass("taigachat_me");
						$(this).contents().find(".taigachat_messagetext").html(XenForo.phrases.dark_ignored);
					}
				});
			}

			if(taigachat_speed){
				$(".taigachat_new .taigachat_absolute_timestamp").each(function(){
					var serverTime = XenForo.serverTimeInfo.now,
						today = XenForo.serverTimeInfo.today,
						todayDow = XenForo.serverTimeInfo.todayDow;
					var calcDow;
					var yesterday = today - 86400;
					var week = today - 6 * 86400;
					var thisTime = parseInt($(this).data('timestamp'), 10);
					var thisDate = new Date(thisTime * 1000);
					var timeString = thisDate.toTimeString().replace(/.*(\d{2}:\d{2}):\d{2}.*/, "$1");

					if (thisTime > today){

						if(taigachat_timedisplay == 'Absolute'){
							$(this).text(XenForo.phrases.today_at_x.replace(/%time%/, timeString));
						} else {
							$(this).text(timeString);
						}

					} else if(thisTime > yesterday){

						$(this).text(XenForo.phrases.yesterday_at_x
								.replace(/%time%/, timeString));

					} else if(thisTime > week){

						calcDow = todayDow - Math.ceil((today - thisTime) / 86400);
						if (calcDow < 0)
						{
							calcDow += 7;
						}

						$(this).text(XenForo.phrases.day_x_at_time_y
							.replace('%day%', XenForo.phrases['day' + calcDow])
							.replace(/%time%/, timeString)
						);
					}

					$(this).text($(this).text() + " - ");

				});
			}

		}

		if(initial || gotNew > 2 || taigachat_lastMessage + 15000 > new Date().getTime()){
			$("#taigachat_box > ol > li.taigachat_new").removeClass("taigachat_new").each(showModerationPopups).css({visibility:"visible"}).show();
		} else {
			$("#taigachat_box > ol > li.taigachat_new").removeClass("taigachat_new").each(showModerationPopups).css({visibility:"visible",display:"none"}).fadeIn(600);
		}

	}

	if(initial || gotNew>0){

		if(taigachat_reverse){
			var total = $("#taigachat_box > ol > li").length;
			total -= taigachat_limit;
			if(total > 0)
				$("#taigachat_box > ol > li").slice(0, total).remove();

			$("#taigachat_box > ol > li").tsort("", {
				order: "asc",
				data: "messageid",
				place: "first"
			});

		} else {
			$("#taigachat_box > ol > li").slice(taigachat_limit).remove();

			$("#taigachat_box > ol > li").tsort("", {
				order: "desc",
				data: "messageid",
				place: "first"
			});
		}

		if(reverse)
			scrollChatBottom(false);

		taigachat_refreshtime = 5;
		restartTimer();

	} else {
		if(!unsync){
			//decayRefreshTime();
			restartTimer();
		}
	}

	// don't count initial load against anti fade
	if(gotNew > 0 && !initial){
		taigachat_lastMessage = new Date().getTime();
	}

}


// jquery context
function showModerationPopups(){
	if( (taigachat_canModify && XenForo.visitor.user_id == $(this).data('userid')) || taigachat_canModifyAll ){
		$(this).children(".Popup").show();

		if(taigachat_canBan && XenForo.visitor.user_id != $(this).data('userid'))
			$("#taigachat_canban_"+$(this).data('messageid')).show();
	}
}

function scrollChatBottom(force){

	if(taigachat_fake)
		return;

	if(force || !taigachat_scrolled || taigachat_lastScroll < new Date().getTime() - 20000 || $("#taigachat_box").get(0).scrollTop >= $("#taigachat_box").get(0).scrollHeight - taigachat_boxHeight - 35){
		var scrollHeight = $("#taigachat_box").get(0).scrollHeight;
		if(typeof scrollHeight == "undefined" || scrollHeight < 100)
			scrollHeight = 99999;
		$("#taigachat_box").get(0).scrollTop = scrollHeight;
	}
	taigachat_scrolled = true;
}

function restartTimer(){

	if(XenForo._hasFocus){
		taigachat_nextRefresh = new Date().getTime() + taigachat_focusedRefreshTime * 1000;
	} else if(taigachat_tabUnfocused){
		taigachat_nextRefresh = new Date().getTime() + taigachat_tabUnfocusedRefreshTime * 1000;
	} else {
		taigachat_nextRefresh = new Date().getTime() + taigachat_unfocusedRefreshTime * 1000;
	}
}

function checkRefresh(){

	if(taigachat_nextRefresh < new Date().getTime()){

		if(taigachat_isRefreshing){
			taigachat_nextRefresh = new Date().getTime();
			return;
		}

		refreshShoutbox(false, false, false);

	}
}



// http://stackoverflow.com/questions/946534/insert-text-into-textarea-with-jquery, modified slightly
jQuery.fn.extend({
	insertAroundCaret: function(myValue, myValue2){
		return this.each(function(i) {
			if(document.selection) {
				this.focus();
				sel = document.selection.createRange();
				sel.text = myValue + sel.text + myValue2;
				this.focus();
			} else if(this.selectionStart || this.selectionStart == '0') {
				var startPos = this.selectionStart;
				var endPos = this.selectionEnd;
				var scrollTop = this.scrollTop;
				this.value = this.value.substring(0, startPos)+myValue+this.value.substring(startPos, endPos)+myValue2+this.value.substring(endPos,this.value.length);
				this.focus();
				this.selectionStart = startPos + myValue.length + myValue2.length + (endPos-startPos);
				this.selectionEnd = startPos + myValue.length + myValue2.length + (endPos-startPos);
				this.scrollTop = scrollTop;
			} else {
				this.value += myValue + myValue2;
				this.focus();
			}
		})
	}
});

// http://blog.vishalon.net/index.php/javascript-getting-and-setting-caret-position-in-textarea/
function getCaretPosition (ctrl) {
	var CaretPos = 0;    // IE Support
	if(document.selection){
		ctrl.focus ();
		var Sel = document.selection.createRange ();
		Sel.moveStart ('character', -ctrl.value.length);
		CaretPos = Sel.text.length;
	}
	// Firefox support
	else if(ctrl.selectionStart || ctrl.selectionStart == '0')
		CaretPos = ctrl.selectionStart;
	return (CaretPos);
}
function getCaretLength (ctrl) {
	var CaretPos = 0;
	if(document.selection){
		ctrl.focus ();
		var Sel = document.selection.createRange ();
		//Sel.moveStart ('character', -ctrl.value.length);
		CaretPos = Sel.text.length;
	}
	else if(ctrl.selectionEnd || ctrl.selectionEnd == '0')
		CaretPos = ctrl.selectionEnd-ctrl.selectionStart;
	return (CaretPos);
}
function setCaretPosition(ctrl, pos){
	if(ctrl.setSelectionRange){
		ctrl.focus();
		ctrl.setSelectionRange(pos,pos);
	}
	else if(ctrl.createTextRange){
		var range = ctrl.createTextRange();
		range.collapse(true);
		range.moveEnd('character', pos);
		range.moveStart('character', pos);
		range.select();
	}
}

/* TinySort 1.4.29
* Copyright (c) 2008-2012 Ron Valstar http://www.sjeiti.com/
*
* Dual licensed under the MIT and GPL licenses:
*   http://www.opensource.org/licenses/mit-license.php
*   http://www.gnu.org/licenses/gpl.html
*/
(function(c){var e=!1,f=null,j=parseFloat,g=Math.min,i=/(-?\d+\.?\d*)$/g,h=[],d=[];c.tinysort={id:"TinySort",version:"1.4.29",copyright:"Copyright (c) 2008-2012 Ron Valstar",uri:"http://tinysort.sjeiti.com/",licensed:{MIT:"http://www.opensource.org/licenses/mit-license.php",GPL:"http://www.gnu.org/licenses/gpl.html"},plugin:function(k,l){h.push(k);d.push(l)},defaults:{order:"asc",attr:f,data:f,useVal:e,place:"start",returns:e,cases:e,forceStrings:e,sortFunction:f}};c.fn.extend({tinysort:function(o,k){if(o&&typeof(o)!="string"){k=o;o=f}var p=c.extend({},c.tinysort.defaults,k),u,D=this,z=c(this).length,E={},r=!(!o||o==""),s=!(p.attr===f||p.attr==""),y=p.data!==f,l=r&&o[0]==":",m=l?D.filter(o):D,t=p.sortFunction,x=p.order=="asc"?1:-1,n=[];c.each(h,function(G,H){H.call(H,p)});if(!t){t=p.order=="rand"?function(){return Math.random()<0.5?1:-1}:function(O,M){var N=e,J=!p.cases?a(O.s):O.s,I=!p.cases?a(M.s):M.s;if(!p.forceStrings){var H=J&&J.match(i),P=I&&I.match(i);if(H&&P){var L=J.substr(0,J.length-H[0].length),K=I.substr(0,I.length-P[0].length);if(L==K){N=!e;J=j(H[0]);I=j(P[0])}}}var G=x*(J<I?-1:(J>I?1:0));c.each(d,function(Q,R){G=R.call(R,N,J,I,G)});return G}}D.each(function(I,J){var K=c(J),G=r?(l?m.filter(J):K.find(o)):K,L=y?""+G.data(p.data):(s?G.attr(p.attr):(p.useVal?G.val():G.text())),H=K.parent();if(!E[H]){E[H]={s:[],n:[]}}if(G.length>0){E[H].s.push({s:L,e:K,n:I})}else{E[H].n.push({e:K,n:I})}});for(u in E){E[u].s.sort(t)}for(u in E){var A=E[u],C=[],F=z,w=[0,0],B;switch(p.place){case"first":c.each(A.s,function(G,H){F=g(F,H.n)});break;case"org":c.each(A.s,function(G,H){C.push(H.n)});break;case"end":F=A.n.length;break;default:F=0}for(B=0;B<z;B++){var q=b(C,B)?!e:B>=F&&B<F+A.s.length,v=(q?A.s:A.n)[w[q?0:1]].e;v.parent().append(v);if(q||!p.returns){n.push(v.get(0))}w[q?0:1]++}}D.length=0;Array.prototype.push.apply(D,n);return D}});function a(k){return k&&k.toLowerCase?k.toLowerCase():k}function b(m,p){for(var o=0,k=m.length;o<k;o++){if(m[o]==p){return !e}}return e}c.fn.TinySort=c.fn.Tinysort=c.fn.tsort=c.fn.tinysort})(jQuery);
/* Array.prototype.indexOf for IE (issue #26) */
if(!Array.prototype.indexOf){Array.prototype.indexOf=function(b){var a=this.length,c=Number(arguments[1])||0;c=c<0?Math.ceil(c):Math.floor(c);if(c<0){c+=a}for(;c<a;c++){if(c in this&&this[c]===b){return c}}return -1}};


//loosely based on http://forrst.com/posts/jQuery_global_plugin_Flashing_window_title-Dt9
var DEFAULT_INTERVAL = 1000;
var original  = document.title;
var newTitle  = document.title;
var timeoutId = undefined;
var flashingActive = false;

var doTheFlash = function(){
	if(flashingActive && !taigachat_focused)
		document.title = (document.title == original) ? newTitle : original;
	else {
		// i hate chrome so much
		document.title = original + ".";
		document.title = original + "..";
		document.title = original;
	}
};

$.flashTitle = function(newMsg, interval) {
	if(newMsg == false){

		document.title = original;
		flashingActive = false;

	} else {

		interval = interval || DEFAULT_INTERVAL;

		flashingActive = true;
		newTitle = newMsg;

		clearInterval(timeoutId);
		timeoutId = setInterval(doTheFlash, interval);
	}
};