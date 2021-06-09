/**
* function related to the acutal login form
*/

(function (){

	document.getElementById("loginButton").addEventListener('click', (event) => {

		var form = event.target.closest("form");
		var url;
		if(form.checkValidity()){
			if (document.getElementById('userType').value == "docente") {
				url = '/verbalizzazione_voti_js/LoginDocente';
			}else{
				url = '/verbalizzazione_voti_js/LoginStudente';
			}

			makeCall("POST", url, event.target.closest("form"), 
				function(req){
					if(req.readyState == XMLHttpRequest.DONE){
						switch(req.status){
							case 200: 
								sessionStorage.setItem('user', req.responseText);
								if (document.getElementById('userType').value == "docente") {
									window.location.href = "homeDocente.html";
								}else{
									window.location.href = "homeStudente.html";
								}
								break;
							default:
								document.getElementById("errorMessage").textContent = "Username e/o password errati";
								break;

						}
					}
				}
			);
			
		}else{
			form.reportValidity();
		}
	});
})();