( function(){

	var courseList,
		appelliList,
		errorMessage,
		esitoEsame,
		generalContainer = document.getElementById("generalContainer");
		pageOrchestrator = new PageOrchestrator();

		window.addEventListener("load", () =>{
			if(sessionStorage.getItem("user") == null){
				window.href.location = "index.html";
			}else{
				pageOrchestrator.start();
			}
		}, false);

		function EsitoEsame(){

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

				generalContainer.innerHTML = "";
				generalContainer.innerHTML = createEsitoEsame();

				//contains the id
				this.id_appello = _appello;
				console.log(_appello);
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
					courseList.update();
					courseList.show();
				});

			}

			this.show = function(){
				errorMessage.hide();

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
								errorMessage.setError(responseMessage);
								errorMessage.show();
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


				//tasti
				
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
										console.log("voto rifiutato, ricarico la pagina");
										self.update(self.id_appello);
										self.show();
									}else{
										errorMessage.setError(responseMessage);
										errorMessage.show();
									}
								}
							})
					});
					console.log("voto rifiutabile");
				}else{
					this.rifiutaButton.setAttribute("hidden", "true");
				}


			}

			this.hide = function(){
				generalContainer.innerHTML = "";
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
				var url = "/verbalizzazione_voti_js/ListaAppelliStudente?id=" + this.course.getId();
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

						//faccio vedere esito esame 
						esitoEsame.update(appelloTemp.getId())
						esitoEsame.show();

						//listaIscritti.update(appelloTemp, self.course.getName(), appello.data);
						//listaIscritti.show();
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
				document.getElementById("studenteName").textContent = sessionStorage.getItem('user');
				errorMessage.hide();
				var self = this;
				makeCall("GET", "/verbalizzazione_voti_js/HomeStudente", null, 
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

			this.start = function(){

				errorMessage = new ErrorMessage();
				courseList = new CourseList();
				appelliList = new AppelliList();
				esitoEsame = new EsitoEsame();

				//richiedo i corsi a cui sono iscritto e faccio vedere la prima parte di home
				courseList.update();

				//registro l'evento di logout
				document.querySelector("a[href='/verbalizzazione_voti_js/Logout']").addEventListener('click', () => {
		      	  		window.sessionStorage.removeItem('user');
		      	  		window.location.href="/verbalizzazione_voti_js/index.html";
	      			}
	      		);

	      		courseList.show();
			}

		}

})();