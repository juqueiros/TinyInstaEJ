dropDown = document.getElementById('navbarDropdown')
container = document.createElement('div')
container.setAttribute('class', 'dropdown-menu')
container.setAttribute('aria-labelledby', "navbarDropdown")

navbar_listUsers()

function navbar_listUsers(){

	var req_navbarUpdateUsers = new XMLHttpRequest()
	req_navbarUpdateUsers.open('GET', 'https://tinyinsta-257116.appspot.com/_ah/api/tinyinsta/v1/users/all?limit=10', true)
	req_navbarUpdateUsers.onload = function() {
		// Begin accessing JSON data here
		var data = JSON.parse(this.response)
		if (req_navbarUpdateUsers.status >= 200 && req_navbarUpdateUsers.status < 400) {
			dropDown.appendChild(container)
			while (container.firstChild) {
		    	container.firstChild.remove();
			}
			for (var i = 0; i < data.items.length; i++) {

				var ddItem = document.createElement('a');
				ddItem.setAttribute('class', 'dropdown-item d-flex justify-content-between')

				if (data.items[i].id == currentUser.id){
					var ddItemUser = document.createElement('a');
					ddItemUser.setAttribute('class', 'font-weight-bold')
					ddItemUser.textContent = data.items[i].userName;

					ddItem.appendChild(ddItemUser)
				}else{
					var ddItemUser = document.createElement('a');
					ddItemUser.setAttribute('onclick', 'connectUser(' + String(data.items[i].id) + ')')

					var ddItemUserName = document.createElement('a')
					ddItemUserName.textContent = data.items[i].userName;
					ddItemUser.appendChild(ddItemUserName)

					var ddItemUserAt = document.createElement('a')
					ddItemUserAt.textContent = " @" + data.items[i].userAt;
					ddItemUserAt.setAttribute('class', 'text-secondary')
					ddItemUserAt.setAttribute('style', 'margin-right:15px;')
					ddItemUser.appendChild(ddItemUserAt)

					var ddItemRemove = document.createElement('a');
					trashIcon = document.createElement('i')
					trashIcon.setAttribute('class', 'fas fa-trash-alt')
					ddItemRemove.setAttribute('onclick', 'removeUser(' + String(data.items[i].id) + ')')
					ddItemRemove.appendChild(trashIcon)

					ddItem.appendChild(ddItemUser)
					ddItem.appendChild(ddItemRemove)
				}
				container.appendChild(ddItem);
			}
		// Adding other options
		var ddItem = document.createElement('div');
		ddItem.setAttribute('class', 'dropdown-divider')
		container.appendChild(ddItem);
		var ddItem = document.createElement('a');
		ddItem.setAttribute('class', 'dropdown-item')
		ddItem.setAttribute('href', "#")
		ddItem.textContent = "Supprimer mon compte";
		container.appendChild(ddItem);
		} else {
			console.error("Impossible d'accéder à l'API")
		}
	}

	req_navbarUpdateUsers.send()
}


function navbar_changeUser(){
	currentUser.id = ""
	currentUser.at = ""
	currentUser.name = ""
	refresh()
}

function navbar_feed(){
	current_tab = 0
	document.getElementById("lb_feed").setAttribute('class', 'nav-link font-weight-bold')
	document.getElementById("lb_own").setAttribute('class', 'nav-link')
	refresh()
}

function navbar_own(){
	current_tab = 1
	document.getElementById("lb_feed").setAttribute('class', 'nav-link')
	document.getElementById("lb_own").setAttribute('class', 'nav-link font-weight-bold')
	refresh()
}