//componente per la visualizzazione dell'esito dell'essame con la possibilità di rifiutare il voto
function EsitoEsame(_pageOrchestrator){

	this.pageOrchestrator = _pageOrchestrator;

	this.container;
	this.body;

	this.corso;
	this.data;

	this.matricola;
	this.cognome;
	this.nome;
	this.email;
	this.corsoLaurea;
	this.voto;

	this.rifiutaButton;
	this.votoRifiutato;

	this.homeButton;

	this.id_appello;

	this.update = function(_appello){

		this.pageOrchestrator.getGeneralContainer().innerHTML = "";
		this.pageOrchestrator.getGeneralContainer().innerHTML = createEsitoEsame();

		//contains the id
		this.id_appello = _appello;
		
		this.container = document.getElementById("container_esito_esame");
		this.body = document.getElementById("body_esito_esame");

		this.corso = document.getElementById("corso");
		this.data = document.getElementById("data");
		this.matricola = document.getElementById("matricola");
		this.cognome = document.getElementById("cognome");
		this.nome = document.getElementById("nome");
		this.email = document.getElementById("email");
		this.corsoLaurea = document.getElementById("corsoLaurea");
		this.voto = document.getElementById("voto");

		this.rifiutaButton = document.getElementById("rifiutaButton");
		this.votoRifiutato = document.getElementById("votoRifiutato");
		this.homeButton = document.getElementById("homeButton");

		//aggiungo listener per home
		//per il rifiuto ho bisogno dei dati dal sever quindi il suo listener viene settato nello show 

		this.homeButton.addEventListener("click", (event) =>{
			this.hide();
			this.pageOrchestrator.getCourseList().update();
			this.pageOrchestrator.getCourseList().show();
		});

	}

	this.show = function(){
		this.pageOrchestrator.getErrorMessage().hide();

		var self= this;
		var url = "/verbalizzazione_voti_js/EsitoEsame?id=" + this.id_appello;

		makeCall("GET", url, null, 
			function(req){
				if(req.readyState == XMLHttpRequest.DONE){
					var responseMessage = req.responseText;

					if(req.status == 200){

						var esitoJson = JSON.parse(responseMessage);

						self.writeEsito(esitoJson);

					}else{
						self.pageOrchestrator.getErrorMessage().setError(responseMessage);
						self.pageOrchestrator.getErrorMessage().show();
					}
				}
			}
		);
	}

	this.writeEsito = function (esitoJson) {
		
		if(!esitoJson.votoPubblicato){
			this.container.innerHTML = "<h3> Il docente non ha ancora pubblicato il voto </h3>";
			return;
		}

		this.corso.textContent = esitoJson.corso;
		this.data.textContent = esitoJson.data;
		this.matricola.textContent = esitoJson.matricola
		this.cognome.textContent = esitoJson.cognome;
		this.nome.textContent = esitoJson.nome;
		this.email.textContent = esitoJson.email;
		this.corsoLaurea.textContent = esitoJson.corso_laurea;
		this.voto.textContent = esitoJson.voto;


		//bottoni
		
		if(esitoJson.votoRifiutato){
			this.rifiutaButton.setAttribute("hidden", "true");
			this.votoRifiutato.textContent = "Il voto e' gia' stato rifiutato";
		}

		;
		if(esitoJson.votoRifiutabile){
			this.rifiutaButton.removeAttribute("hidden");
			this.rifiutaButton.addEventListener("click", (event) => {
				//rifiutare il voto
				var self= this;
				var url = "/verbalizzazione_voti_js/RifiutaVoto?id=" + this.id_appello;

				makeCall("GET", url, null, 
					function(req){
						if(req.readyState == XMLHttpRequest.DONE){
							var responseMessage = req.responseText;
							if(req.status == 200){
								
								self.update(self.id_appello);
								self.show();
							}else{
								self.pageOrchestrator.getErrorMessage().setError(responseMessage);
								self.pageOrchestrator.getErrorMessage().show();
							}
						}
					})
			});
		}else{
			this.rifiutaButton.setAttribute("hidden", "true");
		}


	}

	this.hide = function(){
		this.pageOrchestrator.getGeneralContainer().innerHTML = "";
	}
}