function navbar_validUser(){
	var userInput = document.getElementById('searchbar').value

	var inputSearchbar = document.getElementById('searchbar')
	var btn = document.getElementById('searchbarBtn')

	if(document.activeElement === inputSearchbar){

		var req_getUserName = new XMLHttpRequest()
		req_getUserName.open('GET', "https://tinyinsta-257116.appspot.com/_ah/api/tinyinsta/v1/users/at/" + String(userInput), true)
		req_getUserName.onload = function() {
			// Begin accessing JSON data here
			if (req_getUserName.status >= 200 && req_getUserName.status < 400) {
				if (req_getUserName.status == 204){
					navbar_validUser_press()
				}else{
					inputSearchbar.setAttribute('class', 'form-control mr-sm-2')
					btn.disabled = false
				}
			}
		}
	}

	req_getUserName.send()
}


function navbar_validUser_press(){
	document.getElementById('searchbarBtn').disabled=true
	document.getElementById('searchbar').setAttribute('class', 'form-control mr-sm-2 text-danger')
}


function navbar_searchUser(){
	var label = document.getElementById('userModalLabel')
	var userSB = document.getElementById('searchbar').value

	var user_id = ""
	var user_at = ""

	while (label.firstChild) {
    	label.firstChild.remove();
	}
	var req_getUser = new XMLHttpRequest()
	req_getUser.open('GET', "https://tinyinsta-257116.appspot.com/_ah/api/tinyinsta/v1/users/at/" + userSB, true)
	req_getUser.onload = function() {
		// Begin accessing JSON data here
		if (req_getUser.status == 200) {
			var data = JSON.parse(this.response)
			user_id = data.id
			user_at = data.userAt

			var lbName = document.createElement('div')
			lbName.textContent = data.userName
			var lbAt = document.createElement('div')
			lbAt.setAttribute('class', 'text-secondary')
			lbAt.textContent = "@" + data.userAt
			
			label.appendChild(lbName)
			label.appendChild(lbAt)

			var div = document.getElementById('userModalDiv')
			while (div.firstChild) {
		    	div.firstChild.remove();
			}
			var req_getSub = new XMLHttpRequest()
			req_getSub.open('GET', "https://tinyinsta-257116.appspot.com/_ah/api/tinyinsta/v1/users/" + String(user_id) + "/followers?limit=10" , true)
			req_getSub.onload = function() {
				div.textContent = "Est suivi(e) par : "
				if (req_getSub.status == 200) {
					var data = JSON.parse(this.response)
					for (var i = 0; i < data.items.length; i++) {
						div.textContent += "@" + data.items[i] + ", "
					}
					div.textContent = div.textContent.substring(0, div.textContent.length - 2);
				}
			}
			req_getSub.send()

			var divSub = document.getElementById('userSubButton')

			while (divSub.firstChild) {
		    	divSub.firstChild.remove();
			}

			var req_getFollowings = new XMLHttpRequest()
			req_getFollowings.open('GET', " https://tinyinsta-257116.appspot.com/_ah/api/tinyinsta/v1/users/" + String(currentUser.id) + "/following" , true)
			req_getFollowings.onload = function() {
				if (req_getFollowings.status == 200) {
					var data = JSON.parse(this.response)
					var is_sub = false
					for (var i = 0; i < data.items.length; i++) {
						if(data.items[i] == user_at){
							is_sub = true
							break
						}
					}
					if (!is_sub){
						var subBtn = document.createElement('button')
						subBtn.setAttribute('class', 'btn btn-success')
						subBtn.setAttribute('type', 'button')
						subBtn.textContent = "S'abonner"
					} else {
						divSub.textContent = "Vous êtes abonné"
						divSub.appendChild(document.createElement('br'))
						var subBtn = document.createElement('button')
						subBtn.setAttribute('class', 'btn btn-outline-dark')
						subBtn.setAttribute('type', 'button')
						subBtn.textContent = "Se désabonner"
					}
					subBtn.setAttribute('onclick', 'userSubscibe(' + currentUser.id + ',' + user_id + ')')
					divSub.appendChild(subBtn)
				}
			}
			req_getFollowings.send()
			
		}
	}

	req_getUser.send()
}

function userSubscibe(from, to){
	var req_SubFromTo = new XMLHttpRequest()
	req_SubFromTo.open('PUT', "https://tinyinsta-257116.appspot.com/_ah/api/tinyinsta/v1/users/" + from + "/follow/" + to, true)
	req_SubFromTo.onload = function() {
		if (req_SubFromTo.status == 200) {
			refresh()
		}
	}
	req_SubFromTo.send()
}



function navbar_account(){
	var label = document.getElementById('myAccountModalLabel')

	while (label.firstChild) {
    	label.firstChild.remove();
	}

	var lbName = document.createElement('div')
	lbName.textContent = currentUser.name
	var lbAt = document.createElement('div')
	lbAt.setAttribute('class', 'text-secondary')
	lbAt.textContent = "@" + currentUser.at

	label.appendChild(lbName)
	label.appendChild(lbAt)

	var divSub = document.getElementById('myAccountFollowings')

	while (divSub.firstChild) {
    	divSub.firstChild.remove();
	}

	var req_getFollowings = new XMLHttpRequest()
	req_getFollowings.open('GET', "https://tinyinsta-257116.appspot.com/_ah/api/tinyinsta/v1/users/" + String(currentUser.id) + "/following" , true)
	req_getFollowings.onload = function() {
		if (req_getFollowings.status == 200) {
			var data = JSON.parse(this.response)
			for (var i = 0; i < data.items.length; i++) {
				var li = document.createElement("li")
				li.textContent = data.items[i]
				divSub.appendChild(li)
			}
		}
	}
	req_getFollowings.send()

}

function accountDelete(){
	removeUser(currentUser.id)
	setTimeout(function(){
		navbar_changeUser()}, 500);
}


function publishContent(){
	var req_publish = new XMLHttpRequest()
	var publishDesc = encodeURIComponent(document.getElementById('addPostDesc').value)
	var publishPhoto = encodeURIComponent(document.getElementById('addPostPhoto').value)
	var publishLieu = encodeURIComponent(document.getElementById('addPostLieu').value)
	console.log("https://tinyinsta-257116.appspot.com/_ah/api/tinyinsta/v1/posts/"+ currentUser.id +"/addPost/"+ publishDesc +"/"+ publishPhoto +"?lieu="+ publishLieu )
	req_publish.open('POST', "https://tinyinsta-257116.appspot.com/_ah/api/tinyinsta/v1/posts/"+ currentUser.id +"/addPost/"+ publishDesc +"/"+ publishPhoto +"?lieu="+ publishLieu , true)
	req_publish.onload = function() {
		if (req_publish.status == 200) {
			refresh()
			setTimeout(function(){
				$("#postSuccessModal").modal('show');
			}, 500);
		}
	}
	req_publish.send()
}
