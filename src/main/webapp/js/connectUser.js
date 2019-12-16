function connectUser(id){
	var req_getUser = new XMLHttpRequest()
	req_getUser.open('GET', "https://tinyinsta-257116.appspot.com/_ah/api/tinyinsta/v1/users/" + id, true)
	req_getUser.onload = function() {
		// Begin accessing JSON data here
		if (req_getUser.status == 200) {
			var data = JSON.parse(this.response)
			currentUser.id = data.id
			currentUser.at = data.userAt
			currentUser.name = data.userName
			refresh()
		}
	}
	req_getUser.send()
}

function removeUser(id){
	var req_navbarRemoveUser = new XMLHttpRequest()
	req_navbarRemoveUser.open('DELETE', "https://tinyinsta-257116.appspot.com/_ah/api/tinyinsta/v1/users/delete/" + id, true)
	req_navbarRemoveUser.onload = function() {
		refresh()
	}

	req_navbarRemoveUser.send()
}



function connectUserAtInput(input){
	var at = document.getElementById(input).value
	var req_getUser = new XMLHttpRequest()
	req_getUser.open('GET', "https://tinyinsta-257116.appspot.com/_ah/api/tinyinsta/v1/users/at/" + at, true)
	req_getUser.onload = function() {
		// Begin accessing JSON data here
		if (req_getUser.status == 200) {
			var data = JSON.parse(this.response)
			currentUser.id = data.id
			currentUser.at = data.userAt
			currentUser.name = data.userName
		}
		refresh()
	}
	req_getUser.send()
}


function newuser_validAt(){
	setTimeout(function(){
			newuser_validAt_ex();
		}, 300);
}

function newuser_validAt_ex(){
	var userInput = document.getElementById('tfNewUserAt').value

	var inputSearchbar = document.getElementById('tfNewUserAt')
	var inputSearchbarTf = document.getElementById('tfNewUserAtDesc')
	var btn = document.getElementById('btnCreateUser')

	if(document.activeElement === inputSearchbar){
		var req_getUserAt = new XMLHttpRequest()
		req_getUserAt.open('GET', "https://tinyinsta-257116.appspot.com/_ah/api/tinyinsta/v1/users/at/" + String(userInput), true)
		req_getUserAt.onload = function() {
			if (req_getUserAt.status >= 200 && req_getUserAt.status < 400) {
				if (req_getUserAt.status == 204){
					inputSearchbarTf.textContent = ""
					btn.disabled = false
				}else{
					navbar_validUser_press()
				}
			}
		}

		req_getUserAt.send()
	}
}

function newuser_validAt_press(){
	document.getElementById('btnCreateUser').disabled=true
	document.getElementById('tfNewUserAtDesc').textContent = "Identifiant indisponnible"
}

function createUser(){
	var newUserAt = document.getElementById("tfNewUserAt").value
	var newUserName = document.getElementById("tfNewUserName").value

	var req_createUser = new XMLHttpRequest()
	req_createUser.open('POST', "https://tinyinsta-257116.appspot.com/_ah/api/tinyinsta/v1/users/add/" + newUserAt + "/" + newUserName, true)
	req_createUser.onload = function() {
		if( req_createUser.status == 200){
			var data = JSON.parse(this.response)
			connectUser(data.id)
		}
	}
	req_createUser.send()
}


function likePost(userId, postId){
	var req_like = new XMLHttpRequest()
	req_like.open('PUT', "https://tinyinsta-257116.appspot.com/_ah/api/tinyinsta/v1/user/" + userId + "/like/" + postId, true)
	req_like.onload = function() {
		if( req_like.status == 200){
			likeChangeIcon(postId)
			var postLike = document.getElementById(postId).parentNode
			postLike.childNodes[1].textContent = String(parseInt(postLike.childNodes[1].textContent)+1)
		}
	}
	req_like.send()
}

function unlikePost(userId, postId){
	var req_like = new XMLHttpRequest()
	req_like.open('PUT', "https://tinyinsta-257116.appspot.com/_ah/api/tinyinsta/v1/user/" + userId + "/unlike/" + postId, true)
	req_like.onload = function() {
		if( req_like.status == 200){
			unlikeChangeIcon(postId)
			var postLike = document.getElementById(postId).parentNode
			postLike.childNodes[1].textContent = String(parseInt(postLike.childNodes[1].textContent)-1)
		}
	}
	req_like.send()
}