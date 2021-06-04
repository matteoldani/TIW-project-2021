/**
* IIFE function to set up login event in the homepage
*/

(function() {
	console.log("IIFE inziata");
	document.getElementById("loginDocente").addEventListener('click', (event) => { // (param) => function{}
		console.log("Event happend");
		makeCall("GET", 'HomeDocente', null, 
			function(req) {
				if(req.readyState == XMLHttpRequest.DONE){
					var message = req.responseText;

					//the status will be set to 200 if i've previusly made a login
					//to 403 if i need to log in so i will be redirected to the correct loginPage
					switch(req.status){
						case 200: 
							window.location.href = "homeDocente.html";
							break;
						case 403: 
							window.location.href = "loginDocente.html";
							break;
						default: 
							document.getElementById("errorMessage").textContent = "Errore! Stai provando a fare un'azione non permessa";
							break;
					}

				}
			});
	});
})();
	