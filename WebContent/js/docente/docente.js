( function() {

	var courseList = null,
		appelliList = null,
		listaIscritti = null,
		modificaVoto = null,
		errorMessage = null,
		verbalizzaVoto = null,
		generalContainer = document.getElementById("generalContainer");
	    pageOrchestrator = new PageOrchestrator(); // main controller

	  window.addEventListener("load", () => {
	    if (sessionStorage.getItem("user") == null) {
	      window.location.href = "index.html";
	    } else {
	      pageOrchestrator.start(); // initialize the components
	    } // display initial content
	  }, false);



	function VerbalizzaVoto(){

		this.verbale;
		this.data;
		this.ora;
		this.corso;
		this.dataAppello;

		this.id_appello;

		this.container;
		this.body;

		this.home;
		this.iscrtittiAppello;

		this.update = function(_id_appello){
			generalContainer.innerHTML = "";
			generalContainer.innerHTML = verbalizzatiGenerator();

			this.id_appello = _id_appello;

			this.verbale = document.getElementById("id_verbale");
			this.data = document.getElementById("data_verbale");
			this.ora = document.getElementById("ora_verbale");
			this.corso = document.getElementById("corso_verbale");
			this.dataAppello = document.getElementById("data_appello_verbale");

			this.container = document.getElementById("verbale_container");
			this.body = document.getElementById("body_verbale");

			this.home = document.getElementById("homeButton");
			this.iscrtittiAppello = document.getElementById("iscrittiButton");

			this.home.addEventListener("click", (event) => {
				this.hide();
				courseList.update();
				courseList.show();
			});

			//se schiaccio il bottone per toare alla pagina degli iscitti
			this.iscrtittiAppello.addEventListener("click", (event) => {
				this.hide();
				listaIscritti.update(this.id_appello);
				listaIscritti.show();
			});

		}

		this.show = function(){
			errorMessage.hide();
			var self = this;
			var url = "/verbalizzazione_voti_js/Verbalizzazione?id=" + this.id_appello;

			makeCall("GET", url, null,
				function(req){
					if(req.readyState == XMLHttpRequest.DONE){
						var responseMessage = req.responseText;

						if(req.status == 200){
							var verbalizzatiJson = JSON.parse(responseMessage);

							self.writeVerbalizzati(verbalizzatiJson)
						}else{
							errorMessage.setError(responseMessage);
							errorMessage.show();
						}
					}

				});
		}

		this.writeVerbalizzati = function(verbalizzatiJson){


			this.verbale.textContent = verbalizzatiJson.id_verbale;
			this.data.textContent = verbalizzatiJson.data.day + "/" + verbalizzatiJson.data.month + "/" + verbalizzatiJson.data.year;;
			this.ora.textContent = verbalizzatiJson.ora.hour + "." + verbalizzatiJson.ora.minute;
			this.corso.textContent = verbalizzatiJson.corso;
			this.dataAppello.textContent = verbalizzatiJson.data_appello;
			var self = this;
			verbalizzatiJson.iscrittiVerbalizzati.forEach(function(iscritto){
				var tr, tdMatr, tdVoto;
				tr = document.createElement("tr");
				tdMatr = document.createElement("td");
				tdMatr.textContent = iscritto.studente.matricola;
				tdVoto = document.createElement("td");
				tdVoto.textContent = iscritto.voto;
				tr.appendChild(tdMatr);
				tr.appendChild(tdVoto);

				self.body.appendChild(tr);
			});

		}

		this.hide = function(){
			generalContainer.innerHTML = "";
		}

	}

	function ModificaVoto(){
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



		this.update = function(_id_appello, _matricola){

			generalContainer.innerHTML = "";
			generalContainer.innerHTML = modificaVotoGenerator();
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

				console.log(json);
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
								console.log("evento andatoa buon fine e voto cambiato");

							}else{
								errorMessage.setError(responseMessage);
								errorMessage.show();
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
				listaIscritti.update(this.id_appello);
				listaIscritti.show();
			});
		}

		this.show = function(){

			errorMessage.hide();

			var self = this;
			var url = "/verbalizzazione_voti_js/ModificaVoto?id_appello="+
						this.id_appello +"&&matricola=" + this.matricola;
			makeCall("GET", url, null,
				function(req){
					if(req.readyState == XMLHttpRequest.DONE){

						var responseMessage = req.responseText;
						if(req.status == 200){
							console.log(responseMessage);
							var modificaJson = JSON.parse(responseMessage);
							self.writeModifica(modificaJson);

						}else{
							errorMessage.setError(responseMessage);
							errorMessage.show();
						}
					}
				}
			);
		}

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

				//put the voto in the selct tag
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
		  	generalContainer.innerHTML = "";
		}
	}

	function ListaIscritti(){

		this.divContainer;
		this.container;
		this.body;
		this.appello;

		this.iscrtitti;

		//elementi dell'intestazione
		this.corso;
		this.data;
		this.verbalizza;
		this.home;

		this.pubblica;

		this.multipleInsertion;


		this.activateVerbalizza = function(){
			this.verbalizza.removeAttribute("hidden");
			this.verbalizza.addEventListener("click", (event) => {
				console.log(this.appello);
				verbalizzaVoto.update(this.appello);
				verbalizzaVoto.show();
			});
		}

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
									self.update(self.appello);
									self.show();
									self.success.textContent = responseMessage;
								}else{
									errorMessage.setError(responseMessage);
									errorMessage.show();
								}
							}
						})
				});
			}else{
				this.pubblica.classList.add("grey-background");
			}
		}

		this.checkPubblica = function(){
			var controllo = false;
			this.iscritti.forEach(function(is){
				if(is.stato == "inserito"){
					controllo = true;
				}
			});

			console.log(controllo);
			return controllo;

		}

		this.addModalListener = function(modal){

			window.onclick = function(event) {
			  if (event.target == modal) {
			    modal.style.display = "none";
			    console.log("click");
			  }
			}
		}


		this.update = function(_id_appello, _corso, _dataAppello){
			generalContainer.innerHTML = "";
			generalContainer.innerHTML = listaIscrittiGenerator();

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

			//register to home button an event to relaod the home page
			this.home.addEventListener("click", (event) => {
				this.hide();
				courseList.update();
				courseList.show();
			});

			this.multipleInsertion = document.getElementById("multipleInsertion");
			this.multipleInsertion.addEventListener("click", (event) =>{
				console.log("click multipleInsertion");

				//generalContainer.innerHTML = generalContainer.innerHTML + createModal();
				//generalContainer.insertAdjacentElement("beforeend", createModal());
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
						console.log(rows[i].cells[6].textContent);
						var row = rows[i].cloneNode(true);
						row.cells[7].innerHTML = createVotiSelect();
						modalTableBody.insertAdjacentElement("beforeend", row);

						insertedRows.push(row);
					}
				}

				//aggiungo il listener con la sua funzione per gestire l'inserimento
				var multipleInsertionButton = document.getElementById("multiplePublicButton");
				multipleInsertionButton.addEventListener("click", (event) => {
					//devo prendere tutti i valori messe nelle righe e creare un json con
					//matricola, id_appello, voto

					var allVoti = document.getElementsByClassName("listOfVoti");
					console.log(allVoti);

					var matricole = [];
					var id_appello;
					var voti = [];
					var i=0;
					var hashMatricoleVoti = new Object();

					//devo creare il json
					var json = "{ \"studenti\":[";
					for(i=0; i<allVoti.length-1; i++){
						json = json + "{\"matricola\":\"" + insertedRows[i].cells[0].textContent +
												"\", \"voto\":\""+ allVoti[i].value + "\"},";

						// hashMatricoleVoti[insertedRows[i].cells[0].textContent] = allVoti[i].value;
						// matricole.push(insertedRows[i].cells[0].textContent);


					}
					json = json + "{\"matricola\":\"" + insertedRows[i].cells[0].textContent +
											"\", \"voto\":\""+ allVoti[i].value + "\"}], \"id_appello\": \"" + this.appello+
											"\"}";

					console.log(hashMatricoleVoti);
					console.log(json);

					//devo fare la richeista per madnare il json alla servelet

					var req = new XMLHttpRequest();
					var self = this;
					req.onreadystatechange = function(){
						if(req.readyState == XMLHttpRequest.DONE){
							if(req.status == 200){

								self.update(self.appello, self.corso.textContent, self.data.textContent);
								self.show();
							}else{
								errorMessage.setError(req.responseText);
								errorMessage.show();
							}
							console.log("richiesta mandata e risposta ricevuta");
						}
					}

					req.open("POST", "/verbalizzazione_voti_js/ModificaVoto");
					req.setRequestHeader("Content-Type", "application/json");
					req.send(json);

				});

			});
		}

		this.show = function(){

			errorMessage.hide();

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
							errorMessage.setError(responseMessage);
							errorMessage.show();
						}
					}
				}
			);

			//lo faccio qua perchè devo prima avere gli iscritti

		}

		this.writeIscritti = function(iscrtittiJson){

			var self = this;
			console.log(iscrtittiJson);
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

				tdModifica.addEventListener("click", (event) => {
					console.log("click di modifica del voto");
					self.hide();
					modificaVoto.update(iscritto.appello.id_appello, iscritto.studente.matricola);
					modificaVoto.show();

				});
				tr.appendChild(tdModifica);

				self.body.appendChild(tr);

			});
		}

		this.hide = function(){
			//this.divContainer.setAttribute("hidden", "true");
			generalContainer.innerHTML="";
		}
	}

	function AppelliList(){
		//container e body andranno creati quando viene lanciato l'evento e sanno parte di course List
		this.course;
		this.container;
		this.appelli;

		this.update = function(_course, _container){
			this.course = _course;
			this.container = _container;
		}

		this.show = function(){

			errorMessage.hide();
			var self = this;
			var url = "/verbalizzazione_voti_js/ListaAppelliDocente?id=" + this.course.getId();
			makeCall("GET", url, null,
				function(req){
					if(req.readyState == XMLHttpRequest.DONE){
						var responseMessage = req.responseText;
						if(req.status == 200){
							//CONVERTO IL JSON
							var appelliJson = JSON.parse(responseMessage);

							//controllo se ci sono corsi
							if(appelliJson == null){
								errorMessage.setError("Non hai nessuno appello per questo corso");
								errorMessage.show();
								return;
							}

							self.writeAppelli(appelliJson);
						}else{
							if(req.status == 403){
								window.location.href="index.html";
								return;
							}
							errorMessage.setError(responseMessage);
							errorMessage.show();
						}
					}
				}
			);
		}

		this.writeAppelli = function(appelliJson){
			var ul, li, span;
			this.appelli = [];
			var self = this;
			ul = document.createElement("ul");
			appelliJson.forEach(function(appello){
				var appelloTemp = new Appello(appello.id_appello, appello.id_corso);
				self.appelli.push(appelloTemp);

				li = document.createElement("li");
				span = document.createElement("span");

				span.textContent = appello.data;
				li.appendChild(span);
				ul.appendChild(li);

				li.addEventListener("click", (event) => {
					console.log("click2");
					event.stopPropagation();
					self.hide()
					courseList.hide();
					listaIscritti.update(appelloTemp.getId(), self.course.getName(), appello.data);
					listaIscritti.show();
				});

			});

			this.container.appendChild(ul);

		}

		this.hide = function(){
			//non elimino l'inner html perchè è legato al course list
			this.container.setAttribute("hidden", "true");
		}
	}

	function CourseList(){
		this.container;
		this.body;
		this.courses;
		this.divContainer;

		this.reset = function(){
			errorMessage.hide();
			var elements = document.querySelectorAll('td[class="list-item"');

			elements.forEach( el => {
				//rimuovo tutti gli elementi a parte il primo
				el.innerHTML = el.firstChild.textContent;
			});

		}

		this.update = function(){
			generalContainer.innerHTML = "";
			generalContainer.innerHTML = courseListGenerator();
			this.divContainer = document.getElementById("courseListContainer"),
			this.container = document.getElementById("courseList"),
			this.body =	document.getElementById("courseListBody")

		}

		this.show = function(){

			//imposto nome del professore
			document.getElementById("docenteName").textContent = sessionStorage.getItem('user');
			errorMessage.hide();
			var self = this;
			makeCall("GET", "/verbalizzazione_voti_js/HomeDocente", null,
				function(req){
					if(req.readyState == XMLHttpRequest.DONE){
						var responseMessage = req.responseText;
						if(req.status == 200){
							//CONVERTO IL JSON
							var courseJson = JSON.parse(responseMessage);

							//controllo se ci sono corsi
							if(courseJson == null){
								self.errorMessage.textContent = "Non hai nessuno corso";
								return;
							}

							self.writeCourses(courseJson);
						}else{
							if(req.status == 403){
								window.location.href="index.html";
								return;
							}
							errorMessage.setError(responseMessage);
							errorMessage.show();
						}
					}
				}
			);
		}

		this.writeCourses = function(courseJson){

			this.container.innerHTML = "";
			this.courses = [];
			var self = this;
			courseJson.forEach(function(course){
				var row, cell, span; //messe qui perchè avevo bisogno di mandare la cell
				var courseTemp = new Course(course.nome, course.id_corso)
				self.courses.push(courseTemp);

				row = document.createElement("tr");
				cell = document.createElement("td");
				cell.classList.add("list-item");
				span = document.createElement("span");
				span.textContent = course.nome;
				cell.appendChild(span);
				row.appendChild(cell);

				row.addEventListener("click", (event) => {
					//devo chiedere gli appelli del corso
					console.log("click");
					self.reset();
					appelliList.update(courseTemp, cell);
					appelliList.show();

				});

				self.container.appendChild(row);
			});
			this.container.style.visibility = "visible";
		}

		this.hide = function(){
			generalContainer.innerHTML="";
		}
	}

	function ErrorMessage(){
		this.divContainer = document.getElementById("errorMessageContainer");
		this.message = document.getElementById("errorMessage");

		this.show = function(){
			this.divContainer.removeAttribute("hidden");
		}

		this.setError = function(_errorMessage){
			this.message.textContent = _errorMessage;
		}

		this.getErrorMessage = function() {
			return this.message.textContent;
		}

		this.hide = function() {
			this.divContainer.setAttribute("hidden", "true");
		}

		this.reset = function(){
			this.message.textContent = "";
		}
	}

	function PageOrchestrator(){

		//creo la variabile che contiene l'errore

		//

		this.start = function(){

			errorMessage = new ErrorMessage();
			courseList = new CourseList();
			appelliList = new AppelliList();
			listaIscritti = new ListaIscritti();
			modificaVoto = new ModificaVoto();
			verbalizzaVoto = new VerbalizzaVoto();

			//creo oggetto capace di tenere i corsi al suo interno
			courseList.update();

			document.querySelector("a[href='/verbalizzazione_voti_js/Logout']").addEventListener('click', () => {
	      	  		window.sessionStorage.removeItem('user');
	      	  		window.location.href="/verbalizzazione_voti_js/index.html";
	      		}
	      	);

	      	courseList.show();

	    };
	}

})();
