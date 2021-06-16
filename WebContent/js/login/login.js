/**
* function related to the acutal login form
*/

(function (){

	document.getElementById("loginButton").addEventListener('click', (event) => {

		//prendo il form della login
		var form = event.target.closest("form");
		var url;

		//se i valori del form sono insetiti
		if(form.checkValidity()){
			//in base al tipoo di utente chiamo una servlet diversa
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
								//se il login è andato a buon fine allora salvo lo user nella sessione e vado alla sua home
								sessionStorage.setItem('user', req.responseText);
								if (document.getElementById('userType').value == "docente") {
									window.location.href = "homeDocente.html";
								}else{
									window.location.href = "homeStudente.html";
								}
								break;
							default:
								//stampo l'errore se non è andato a buon fire il login
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