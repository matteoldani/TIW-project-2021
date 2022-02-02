	//componente per la gestione della modifica del singolo voto
	function ModificaVoto(_pageOrchestrator){

		this.pageOrchestrator = _pageOrchestrator;

		this.divContainer;
		this.modificaTitle;
		this.container;
		this.datoMatricola;
		this.datoCognome;
		this.datoNome;
		this.datoEmail;
		this.datoCL;

		this.datoVotoModificabile;
		this.datoVotoVisualizzabile;
		this.votoVisualizzabile;
		this.voto;

		this.id_appello;
		this.matricola;

		this.eventRegistered = false;



		this.update = function(_id_appello, _matricola, _corso, _dataAppello){

			this.pageOrchestrator.getGeneralContainer().innerHTML = "";
			this.pageOrchestrator.getGeneralContainer().innerHTML = modificaVotoGenerator();

			this.divContainer = document.getElementById("modificaEsitoContainer");
			this.modificaTitle = document.getElementById("modificaTitle");
			this.container = document.getElementById("modificaVoto");
			this.datoMatricola = document.getElementById("datoMatricola");
			this.datoCognome = document.getElementById("datoCognome");
			this.datoNome = document.getElementById("datoNome");
			this.datoEmail = document.getElementById("datoEmail");
			this.datoCL = document.getElementById("datoCL");

			this.datoVotoModificabile = document.getElementById("datoVotoModificabile");
			this.datoVotoVisualizzabile = document.getElementById("datoVotoVisualizzabile");
			this.votoVisualizzabile = document.getElementById("votoVisualizzabile");
			this.voto = document.getElementById("voto");

			this.id_appello = _id_appello;
			this.matricola = _matricola;

			this.corso = _corso;
			this.dataAppello = _dataAppello;


			document.getElementById("modificaEsito").addEventListener("click", (event) => {
				//imposto parametri del form
				document.getElementById("matricola_hidden").value = this.matricola;
				document.getElementById("id_appello_hidden").value = this.id_appello;

				//prendo il form e faccio la call
				var form = document.getElementById("formModifica");
				var matricolaHidden = document.getElementById("matricola_hidden").value;
				var voto = document.getElementById("voto").value;

				//creo il json da mandare come post
				var json = "{ \"studenti\":[{\"matricola\":\""+matricolaHidden+
										"\",\"voto\":\"" + voto +"\"}],\"id_appello\":\""+
									this.id_appello + "\"}";

				if(form.checkValidity()){

					var req = new XMLHttpRequest();
					var self = this;

					req.onreadystatechange = function(){
						if(req.readyState == XMLHttpRequest.DONE){
							var responseMessage = req.responseText;
							if (req.status == 200) {
								var e = document.createEvent("HTMLEvents");
								e.initEvent("click", false, true);
								document.getElementById("backToIscritti").dispatchEvent(e);
							}else{
								self.pageOrchestrator.getErrorMessage().setError(responseMessage);
								self.pageOrchestrator.getErrorMessage().show();
							}
						}
					}

					req.open("POST", "/verbalizzazione_voti_js/ModificaVoto");
					req.setRequestHeader("Content-Type", "application/json");
					req.send(json);

				}
			});


			//se schiaccio il bottone per toare alla pagina degli iscitti
			document.getElementById("backToIscritti").addEventListener("click", (event) => {
				this.hide();
				this.pageOrchestrator.getListaIscritti().update(this.id_appello, this.corso, this.dataAppello);
				this.pageOrchestrator.getListaIscritti().show();
			});
		}

		this.show = function(){

			this.pageOrchestrator.getErrorMessage().hide();

			var self = this;
			var url = "/verbalizzazione_voti_js/ModificaVoto?id_appello="+
						this.id_appello +"&&matricola=" + this.matricola;
			makeCall("GET", url, null,
				function(req){
					if(req.readyState == XMLHttpRequest.DONE){

						var responseMessage = req.responseText;
						if(req.status == 200){
							var modificaJson = JSON.parse(responseMessage);
							self.writeModifica(modificaJson);

						}else{
							self.pageOrchestrator.getErrorMessage().setError(responseMessage);
							self.pageOrchestrator.getErrorMessage().show();
						}
					}
				}
			);
		}

		//funzione che gestisce il display del bottono modifica in base allo stato del voto
		//potrebbe essere poossibile solo una visualizzazione e non una modifica
		this.writeModifica = function(modificaJson){
			var modifica = true;
			if(modificaJson.stato == "verbalizzato" || modificaJson.stato == "pubblicato"){
				modifica = false;
			}

			if(modifica){
				this.modificaTitle.textContent = "Modifica Voto";
			}else{
				this.modificaTitle.textContent = "Visualizza Voto";
			}

			this.datoMatricola.textContent = modificaJson.matricola;
			this.datoCognome.textContent = modificaJson.cognome;
			this.datoNome.textContent = modificaJson.nome;
			this.datoEmail.textContent = modificaJson.email;
			this.datoCL.textContent = modificaJson.corso_laurea;

			if(modifica){
				this.datoVotoVisualizzabile.setAttribute("hidden", "true");
				this.datoVotoModificabile.removeAttribute("hidden");

				//metto il voto nella select 
				var opts = this.voto.options;
				for (var opt, j = 0; opt = opts[j]; j++) {
				  if (opt.value == modificaJson.voto) {
				    this.voto.selectedIndex = j;
				    break;
				  }
				}


			}else{
				this.datoVotoVisualizzabile.removeAttribute("hidden");
				this.datoVotoModificabile.setAttribute("hidden", "true");
				this.votoVisualizzabile.textContent = modificaJson.voto;
			}

		}

		this.hide = function(){
		  	this.pageOrchestrator.getGeneralContainer().innerHTML = "";
		}
	}
