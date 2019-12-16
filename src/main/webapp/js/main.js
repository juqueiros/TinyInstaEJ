function main_splash(){

	/*var req_getAllPub = new XMLHttpRequest()
	req_getAllPub.open('POST', "https://tinyinsta-257116.appspot.com/_ah/api/tinyinsta/v1/posts/5646874153320448/addPost/Le%20sud%20du%20massif%20des%20Coyote%20Buttes%20(Arizona)%2C%20vu%20depuis%20le%20lieu-dit%20de%20Cottonwood%20Cove./https%3A%2F%2Fwww.auvergnerhonealpes.fr%2Fuploads%2FImage%2F5c%2FIMF_100%2FGAB_CRRAA%2F754_933_Paysage-du-Velay.jpg?lieu=Arizona", true)
	req_getAllPub.onload = function() {
		console.log(req_getAllPub.status)
		console.log(this.response)
	}
	req_getAllPub.send()*/

	setTimeout(function(){
		if (currentUser.id == ""){
		$("#firstModal").modal('show');
	}}, 500);
	main_connectedAs()
	if (current_tab == 0){https://images.pexels.com/photos/207962/pexels-photo-207962.jpeg?auto=comp
		main_feed()
	}else if (current_tab == 1){
		main_own()
	}

}


function main_own(){
	main_postsDisplay("https://tinyinsta-257116.appspot.com/_ah/api/tinyinsta/v1/posts/user/" + String(currentUser.at) + "/recent?limit=20")
}



function main_feed(){
	main_postsDisplay("https://tinyinsta-257116.appspot.com/_ah/api/tinyinsta/v1/posts/" + String(currentUser.id) + "/timeline?limit=20")
}


function main_postsDisplay(requestStr){
	var root = document.getElementById("root")

	var request = new XMLHttpRequest()
	request.open('GET', requestStr, true)
	request.onload = function() {
		while (root.firstChild) {
    		root.firstChild.remove();
		}
		if (request.status == 200) {
			var postIdArray = []
			var data = JSON.parse(this.response)
			for (var i = 0; i < data.items.length; i++) {
				var postUserIcon = document.createElement('i')
				postUserIcon.setAttribute('class', 'fa fa-user')
				postUserIcon.setAttribute('style', 'margin-right:20px;')
				var postUserName = document.createElement('a')
				postUserName.textContent = data.items[i].at
				var postUser = document.createElement('h2')
				postUser.appendChild(postUserIcon)
				postUser.appendChild(postUserName)
				var row = document.createElement('div')
				row.setAttribute('class', 'row')
				var post = document.createElement('div')
				post.setAttribute('class', 'col-sm text-left rounded shadow p-4 mb-4 bg-white border ti-picture')
				var postDesc = document.createElement('h5')
				postDesc.textContent = data.items[i].description
				var postLocation = document.createElement('p')
				postLocation.setAttribute('style', 'margin-left:50px;')
				postLocation.textContent = data.items[i].lieu
				var postImg = document.createElement('img')
				postImg.setAttribute('src', data.items[i].photo)
				postImg.setAttribute('onerror', "this.src='resources/default.png';")
				postImg.setAttribute('class', 'img-responsive text-center')
				postImg.setAttribute('style', 'width:100%;')
				var postLikeIcon = document.createElement('i')
				postLikeIcon.setAttribute('class', 'far fa-heart text-danger')
				postLikeIcon.setAttribute('style', 'font-size:30px; margin:10px;')
				postLikeIcon.setAttribute('id', String(data.items[i].id))
				var postLikeCount = document.createElement('a')
				postLikeCount.setAttribute('style', 'font-size:20px;')
				postLikeCount.textContent = 0
				try {postLikeCount.textContent = data.items[i].likedBy.length
					console.log(data.items[i].likedBy)}
				catch (error){}
				var postLike = document.createElement('a')
				postLike.setAttribute('onclick', 'likePost('+ currentUser.id +','+ String(data.items[i].id) +')')
				postLike.appendChild(postLikeIcon)
				postLike.appendChild(postLikeCount)
				var postDate = document.createElement('p')
				postDate.setAttribute('class', 'text-secondary')
				const date = new Date(data.items[i].date);
				var postDateString = "Le " + date.getDate() + ' / ' + ("0" + (date.getMonth() + 1)).slice(-2) + ' à ' + ("0" + date.getHours()).slice(-2) + " : " + ("0" + date.getMinutes()).slice(-2)
				postDate.textContent = postDateString

				post.appendChild(postUser)
				post.appendChild(postLocation)
				post.appendChild(postImg)
				post.appendChild(postLike)
				post.appendChild(postDesc)
				post.appendChild(postDate)
				row.appendChild(post)
				root.appendChild(row)

				postIdArray.push(data.items[i].id)
			}
			isPostLiked(postIdArray)
		}
	}
	request.send()
}


function isPostLiked(postArray){
	if (postArray.length > 0){
		// Change icon if the post is liked
		var req_isLiked = new XMLHttpRequest()
		req_isLiked.open('GET', "https://tinyinsta-257116.appspot.com/_ah/api/tinyinsta/v1/posts/"+ currentUser.id +"/liked?postId=" + String(postArray[0]), true)
		var idLike = String(postArray[0])
		req_isLiked.onload = function() {
			if (req_isLiked.status == 200) {
				likeChangeIcon(idLike)
				var postLike = document.getElementById(idLike).parentNode
				//postLike.childNodes[1].textContent = String(parseInt(postLike.childNodes[1].textContent)+1)
			}
			postArray.shift()
			isPostLiked(postArray)
		}
		req_isLiked.send()
	}
}



function main_connectedAs(){
	const title = document.getElementById('connectedAs')
	var req_getUserName = new XMLHttpRequest()
	req_getUserName.open('GET', "https://tinyinsta-257116.appspot.com/_ah/api/tinyinsta/v1/users/" + String(currentUser.id), true)
	req_getUserName.onload = function() {
		if (req_getUserName.status == 200) {
			var data = JSON.parse(this.response)
			title.textContent = data.userName
		} else {
			title.textContent = "..."
		}
	}
	req_getUserName.send()
}


function likeChangeIcon(divId){
	var likeIcon = document.getElementById(divId)
	likeIcon.setAttribute('class', 'fas fa-heart text-danger')
	likeIcon.setAttribute('style', 'font-size:30px; margin:10px;')
	var postLike = likeIcon.parentNode
	postLike.setAttribute('onclick', 'unlikePost('+ currentUser.id +','+ divId +')')
}

function unlikeChangeIcon(divId){
	var likeIcon = document.getElementById(divId)
	likeIcon.setAttribute('class', 'far fa-heart text-danger')
	likeIcon.setAttribute('style', 'font-size:30px; margin:10px;')
	var postLike = likeIcon.parentNode
	postLike.setAttribute('onclick', 'likePost('+ currentUser.id +','+ divId +')')
}