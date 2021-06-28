/**
* IIFE function to set up login event in the homepage
*/

(function() {
	
	
	document.getElementById("loginDocente").addEventListener('click', (event) => { // (param) => function{}
		
		makeCall("GET", 'HomeDocente', null, 
			function(req) {
				if(req.readyState == XMLHttpRequest.DONE){
					var message = req.responseText;

					//the status will be set to 200 if i've previusly made a login
					//to 403 if i need to log in so i will be redirected to the correct loginPage
					switch(req.status){
						case 200: 
							window.location.href = "homeDocente.html";
							if(sessionStorage.getItem("user")!= null){
								window.location.href = "homeDocente.html";
							}else{
								window.location.href = "loginDocente.html";
							}
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


	document.getElementById("loginStudente").addEventListener('click', (event) => { // (param) => function{}
		
		makeCall("GET", 'HomeStudente', null, 
			function(req) {
				if(req.readyState == XMLHttpRequest.DONE){
					var message = req.responseText;

					//the status will be set to 200 if i've previusly made a login
					//to 403 if i need to log in so i will be redirected to the correct loginPage
					switch(req.status){
						case 200: 
							if(sessionStorage.getItem("user")!= null){
								window.location.href = "homeStudente.html";
							}else{
								window.location.href = "loginStudente.html";
							}
							
							break;
						case 403: 
							window.location.href = "loginStudente.html";
							break;
						default: 
							document.getElementById("errorMessage").textContent = "Errore! Stai provando a fare un'azione non permessa";
							break;
					}

				}
			});
	});
})();
	