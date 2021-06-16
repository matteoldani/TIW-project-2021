	//componente per la gestioone della lista degli iscritti e della pagina modale
	function ListaIscritti(_pageOrchestrator){

		this.pageOrchestrator = _pageOrchestrator;

		this.divContainer;
		this.container;
		this.body;
		this.appello;

			
		this.dataAppello;

		this.iscrtitti;

		//elementi dell'intestazione
		this.corso;
		this.data;
		this.verbalizza;
		this.home;

		this.pubblica;

		this.multipleInsertion;


		//funzinoe che chiama la verbalizzazione dei voti
		this.activateVerbalizza = function(){
			this.verbalizza.removeAttribute("hidden");
			this.verbalizza.addEventListener("click", (event) => {
				this.pageOrchestrator.getVerbalizzaVoto().update(this.appello);
				this.pageOrchestrator.getVerbalizzaVoto().show();
			});
		}

		//funzione che controlla se ci sono voti verbalizzabili
		this.checkVerbalizza = function(){
			var controllo = false;
			this.iscritti.forEach(function(is){
				if(is.stato == "pubblicato"){
					controllo = true;
				}
			});

			if(controllo == true){
				this.activateVerbalizza();
			}
		}

		//funzione che gestisce la pubblicazione dei voti
		this.activatePubblica = function(){
			var controllo = this.checkPubblica();
			if(controllo == true){
				this.pubblica.classList.add("green-background");
				this.pubblica.addEventListener("click", (event) =>{

					//creo un ffinto form da mandare come paramentro della richeista di post
					var form = document.createElement("form");
					//creo l'input type da mandare
					var input = document.createElement("input");
					//immposto name e value
					input.setAttribute("value", this.appello);
					input.setAttribute("name", "id");
					form.appendChild(input);

					var self = this;
					makeCall("POST", "/verbalizzazione_voti_js/Pubblicazione", form,
						function(req){
							if(req.readyState == XMLHttpRequest.DONE){
								var responseMessage = req.responseText;

								if(req.status == 200){
									self.hide();
									self.update(self.appello, self.corso.textContent, self.dataAppello);
									self.show();
									self.success.textContent = responseMessage;
								}else{
									self.pageOrchestrator.getErrorMessage().setError(responseMessage);
									self.pageOrchestrator.getErrorMessage().show();
								}
							}
						})
				});
			}else{
				this.pubblica.classList.add("grey-background");
			}
		}

		//funzioone che controlla se ci sono voti pubblicabili
		this.checkPubblica = function(){
			var controllo = false;
			this.iscritti.forEach(function(is){
				if(is.stato == "inserito"){
					controllo = true;
				}
			});

			return controllo;

		}

		//funzione che aggiunge il listener per rimuovere il modal
		this.addModalListener = function(modal){

			window.onclick = function(event) {
			  if (event.target == modal) {
			    modal.style.display = "none";
			  }
			}
		}


		this.update = function(_id_appello, _corso, _dataAppello){
			this.pageOrchestrator.getGeneralContainer().innerHTML = "";
			this.pageOrchestrator.getGeneralContainer().innerHTML = listaIscrittiGenerator();

			//chiamo sort ogni volta che ricarico questa pagina
			makeElementSortable();

			this.appello = _id_appello;
			this.divContainer = document.getElementById("iscrittiAppelloContainer");
			this.container = document.getElementById("iscrittiAppello");
			this.body = document.getElementById("iscrittiAppelloBody");


			this.iscrtitti = null;

			//elementi dell'intestazione
			this.success = document.getElementById("successMessage");
			this.corso = document.getElementById("courseName");
			this.data = document.getElementById("appelloDate");
			this.verbalizza = document.getElementById("verbalizzaButton");
			this.home = document.getElementById("homeButton");
			this.pubblica = document.getElementById("pubblicaButton");

			this.corso.textContent = _corso;
			this.data.textContent = _dataAppello;
			this.dataAppello = _dataAppello;

			//register to home button an event to relaod the home page
			this.home.addEventListener("click", (event) => {
				this.hide();
				this.pageOrchestrator.getCourseList().update();
				this.pageOrchestrator.getCourseList().show();
			});

			this.multipleInsertion = document.getElementById("multipleInsertion");
			//rimuovo possibili event listeners
			//this.multipleInsertion.replaceWith(this.multipleInsertion.cloneNode(false));
			this.multipleInsertion.addEventListener("click", (event) =>{

				var modal = document.getElementById("modal");

				this.addModalListener(modal);

				modal.style.display = "block";

				//devo prendere tutti gli elementi non inserito della tabella originale
				var rows = document.getElementById("iscrittiAppello").rows;
				var insertedRows = [];
				var modalTableBody = document.getElementById("modal-table-body");

				//elimino le righe precedenti
				modalTableBody.innerHTML = "";

				//inserisco le righe che non hanno ancora il voto inserito
				var i =0;
				for(i=1; i<rows.length; i++){
					if(rows[i].cells[6].textContent == "non inserito"){
						
						var row = rows[i].cloneNode(true);
						row.cells[7].innerHTML = createVotiSelect();
						modalTableBody.insertAdjacentElement("beforeend", row);

						insertedRows.push(row);
					}
				}

				//aggiungo il listener con la sua funzione per gestire l'inserimento
				var multipleInsertionButton = document.getElementById("multiplePublicButton");
				multipleInsertionButton.replaceWith(multipleInsertionButton.cloneNode(true));
				var multipleInsertionButton = document.getElementById("multiplePublicButton");
				multipleInsertionButton.addEventListener("click", (event) => {
					//devo prendere tutti i valori messe nelle righe e creare un json con
					//matricola, id_appello, voto

					var allVoti = document.getElementsByClassName("listOfVoti");

					var matricole = [];
					var id_appello;
					var voti = [];
					var i=0;

					//devo creare il json
					var json = "{ \"studenti\":[";
					for(i=0; i<allVoti.length-1; i++){
						json = json + "{\"matricola\":\"" + insertedRows[i].cells[0].textContent +
												"\", \"voto\":\""+ allVoti[i].value + "\"},";
					}
					json = json + "{\"matricola\":\"" + insertedRows[i].cells[0].textContent +
											"\", \"voto\":\""+ allVoti[i].value + "\"}], \"id_appello\": \"" + this.appello+
											"\"}";


					//devo fare la richeista per madnare il json alla servelet

					var req = new XMLHttpRequest();
					var self = this;
					req.onreadystatechange = function(){
						if(req.readyState == XMLHttpRequest.DONE){
							if(req.status == 200){

								self.update(self.appello, self.corso.textContent, self.data.textContent);
								self.show();
							}else{
								self.pageOrchestrator.getErrorMessage().setError(req.responseText);
								self.pageOrchestrator.getErrorMessage().show();
							}
						}
					}

					req.open("POST", "/verbalizzazione_voti_js/ModificaVoto");
					req.setRequestHeader("Content-Type", "application/json");
					req.send(json);

				});

			});
		}

		this.show = function(){

			this.pageOrchestrator.getErrorMessage().hide();

			var self = this;
			var url = "/verbalizzazione_voti_js/IscrittiAppello?id=" + this.appello;
			makeCall("GET", url, null,
				function(req){
					if(req.readyState == XMLHttpRequest.DONE){
						var responseMessage = req.responseText;

						if(req.status == 200){

							if(responseMessage == null){
								//non ci sono iscrtitti al corso

								return;
							}

							var iscrtittiJson = JSON.parse(responseMessage);
							self.iscritti = iscrtittiJson;
							self.checkVerbalizza();
							self.activatePubblica();
							self.writeIscritti(iscrtittiJson);

						}else{
							if(req.status == 403){
								window.location.href="index.html";
								return;
							}
							self.pageOrchestrator.getErrorMessage().setError(responseMessage);
							self.pageOrchestrator.getErrorMessage().show();
						}
					}
				}
			);

			//lo faccio qua perchè devo prima avere gli iscritti

		}

		this.writeIscritti = function(iscrtittiJson){

			var self = this;
			iscrtittiJson.forEach(function(iscritto){
				//controllate che non servano tutti td diersi
				var tdMatricola, tdCognome, tdNome, tdCL, tdMail, tdVoto, tdStato, tdModifica, tr;

				tr = document.createElement("tr");


				tdMatricola = document.createElement("td");
				tdMatricola.textContent = iscritto.studente.matricola;
				tr.appendChild(tdMatricola);

				tdCognome = document.createElement("td");
				tdCognome.textContent = iscritto.studente.cognome;
				tr.appendChild(tdCognome);

				tdNome = document.createElement("td");
				tdNome.textContent = iscritto.studente.nome;
				tr.appendChild(tdNome);

				tdMail = document.createElement("td");
				tdMail.textContent = iscritto.studente.email;
				tr.appendChild(tdMail);

				tdCL = document.createElement("td");
				tdCL.textContent = iscritto.studente.corso_laurea;
				tr.appendChild(tdCL);


				tdVoto = document.createElement("td");
				tdVoto.textContent = iscritto.voto;
				tr.appendChild(tdVoto);

				tdStato = document.createElement("td");
				tdStato.textContent = iscritto.stato;
				tr.appendChild(tdStato);

				tdModifica = document.createElement("td");
				if(tdStato.textContent == "pubblicato" || tdStato.textContent == "verbalizzato"){
					tdModifica.textContent = "Visualizza";
				}else{
					tdModifica.textContent = "Modifica";
				}
				tdModifica.setAttribute("class", "modifica-underline");
				tdModifica.addEventListener("click", (event) => {
					
					self.hide();
					self.pageOrchestrator.getModificaVoto().update(iscritto.appello.id_appello, iscritto.studente.matricola, self.corso.textContent, self.dataAppello);
					self.pageOrchestrator.getModificaVoto().show();

				});
				tr.appendChild(tdModifica);

				self.body.appendChild(tr);

			});
		}

		this.hide = function(){
			//this.divContainer.setAttribute("hidden", "true");
			this.pageOrchestrator.getGeneralContainer().innerHTML="";
		}
	}