( function() {

	var courseList,
		appelliList,
		listaIscritti,
		homeButton,
	    pageOrchestrator = new PageOrchestrator(); // main controller

	  window.addEventListener("load", () => {
	    if (sessionStorage.getItem("user") == null) {
	      window.location.href = "index.html";
	    } else {
	      pageOrchestrator.start(); // initialize the components
	      pageOrchestrator.refresh();
	    } // display initial content
	  }, false);

	
	function ListaIscritti(_errorMessage, _appello){
		this.errorMessage = _errorMessage;
		this.divContainer = document.getElementById("iscrittiAppelloContainer");
		this.container = document.getElementById("iscrittiAppello");
		this.body = document.getElementById("iscrittiAppelloBody");
		this.appello = _appello;

		this.iscrtitti = null;

		//elementi dell'intestazione 
		this.corso = document.getElementById("curseName");
		this.data = document.getElementById("appelloDate");
		this.verbalizza = document.getElementById("verbalizzaButton");
		this.home = document.getElementById("homeButton");

		//register to home button an event to relaod the home page 
		this.home.addEventListener("click", (event) => {
			this.hide();
			courseList.show();
		});

		this.activateVerbalizza = function(){
			this.verbalizza.removeAttribute("hidden");
			this.verbalizza.addEventListener("click", this.verbalizzaAppello());
		}

		this.verbalizzaAppello = function(){
			//farò le cose necessarie alla verbalizzione dell'appelo
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

		this.show = function(){
			this.container.style.visibility = "visible";
			this.divContainer.removeAttribute("hidden");

			var self = this;
			var url = "/verbalizzazione_voti_js/IscrittiAppello?id=" + this.appello.getId();
			makeCall("GET", url, null, 
				function(req){
					if(req.readyState == XMLHttpRequest.DONE){
						var responseMessage = req.responseText;

						if(req.status == 200){

							if(responseMessage == null){
								//non ci sono iscrtitti al corso
								//stampa messaggio
								return;
							}

							var iscrtittiJson = JSON.parse(responseMessage);
							self.iscritti = iscrtittiJson;
							console.log(self);
							self.checkVerbalizza();
							self.writeIscritti(iscrtittiJson);

						}else{
							if(req.status == 403){
								window.location.href="index.html";
								return;
							}
							self.errorMessage.textContent = responseMessage;
						}
					}
				}
			);
		}

		this.writeIscritti = function(iscrtittiJson){

			this.body.innerHTML = "";

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
				tdModifica.textContent = "Modifica";
				tdMatricola.addEventListener("click", (event) => {
					console.log("click di modifica del voto");
				});
				tr.appendChild(tdModifica);

				self.body.appendChild(tr);

			});

		}

		this.hide = function(){
			this.divContainer.setAttribute("hidden", "true");	
		}

	}

	function Course(_name, _id){
		this.name = _name;
		this.id = _id;

		this.getName = function(){ return this.name;}
		this.getId = function(){ return this.id;}
	}

	function Appello(_id, _course_id){
		this.course_id = _course_id;
		this.id = _id;

		this.getId = function(){
			return this.id;
		}
		this.getCourse_id = function(){
			return this.course;
		}
	}

	function AppelliList(_course, _container, _errorMessage){ 
		//container e body andranno creati quando viene lanciato l'evento e sanno parte di course List
		this.course = _course;
		this.container = _container;
		this.errorMessage = _errorMessage;
		this.appelli = [];



		this.show = function(){

			this.container.removeAttribute("hidden");

			var self = this;
			var url = "/verbalizzazione_voti_js/ListaAppelli?id=" + this.course.getId();
			makeCall("GET", url, null, 
				function(req){
					if(req.readyState == XMLHttpRequest.DONE){
						var responseMessage = req.responseText;
						if(req.status == 200){
							//CONVERTO IL JSON
							var appelliJson = JSON.parse(responseMessage);
							
							//controllo se ci sono corsi
							if(appelliJson == null){
								self.errorMessage.textContent = "Non hai nessuno appello per questo corso";
								return;
							} 

							self.writeAppelli(appelliJson);
						}else{
							if(req.status == 403){
								window.location.href="index.html";
								return;
							}
							self.errorMessage.textContent = responseMessage;
						}
					}
				} 
			);
		}

		this.writeAppelli = function(appelliJson){
			var ul, li, span;

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

					courseList.hide();
					listaIscritti = new ListaIscritti(self.errorMessage, appelloTemp);
					listaIscritti.show();
				});

			});

			this.container.appendChild(ul);
			this.container.style.visibility = "visible";
			ul.style.visibility = "visible";

		}
	}

	function CourseList(_errorMessage, _divContainer, _container, _body){
		this.errorMessage = _errorMessage;
		this.container = _container;
		this.body = _body;
		this.courses = [];
		this.divContainer = _divContainer;

		this.reset = function(){
			var elements = document.querySelectorAll('td[class="list-item"');
			console.log(elements);

			elements.forEach( el => {
				//rimuovo tutti gli elementi a parte il primo
				el.innerHTML = el.firstChild.textContent;
			});
					
		}

		this.show = function(){
			this.divContainer.removeAttribute("hidden");
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
							self.errorMessage.textContent = responseMessage;
						}
					}
				}
			);
		};

		this.writeCourses = function(courseJson){
			
			this.container.innerHTML = "";
			//this.body.innerHTML = "";

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
					appelliList = new AppelliList(courseTemp, cell, self.errorMessage);
					appelliList.show();

				});

				self.container.appendChild(row);
			});
			this.container.style.visibility = "visible";
		}

		this.hide = function(){
			this.divContainer.setAttribute("hidden", "true");


		}
	}

	

	function PageOrchestrator(){

		//creo la variabile che contiene l'errore 
		var errorMessage = document.getElementById("errorMessage");
		//

		this.start = function(){
			//imposto nome del professore 
			document.getElementById("docenteName").textContent = sessionStorage.getItem('user');

			//creo oggetto capace di tenere i corsi al suo interno
			courseList = new CourseList(
				errorMessage, 
				document.getElementById("courseListContainer"),
				document.getElementById("courseList"),
				document.getElementById("courseListBody")
				);

			document.querySelector("a[href='/verbalizzazione_voti_js/Logout']").addEventListener('click', () => {
	      	  		window.sessionStorage.removeItem('user');
	      		}
	      	);

	      	courseList.show();

	    };


		this.refresh = function(){

		}
	}

})();