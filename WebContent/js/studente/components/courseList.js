//componente per la lista dei corsi
function CourseList(_pageOrchestrator){

	this.pageOrchestrator = _pageOrchestrator;

	this.container;
	this.body;
	this.courses;
	this.divContainer;

	this.reset = function(){
		this.pageOrchestrator.getErrorMessage().hide();
		var elements = document.querySelectorAll('td[class="list-item"');
		
		elements.forEach( el => {
			//rimuovo tutti gli elementi a parte il primo
			el.innerHTML = el.firstChild.textContent;
		});
				
	}

	this.update = function(){
		this.pageOrchestrator.getGeneralContainer().innerHTML = "";
		this.pageOrchestrator.getGeneralContainer().innerHTML = courseListGenerator();
		this.divContainer = document.getElementById("courseListContainer"),
		this.container = document.getElementById("courseList"),
		this.body =	document.getElementById("courseListBody")
		
	}

	this.show = function(){
	
		//imposto nome dello studente 
		document.getElementById("studenteName").textContent = sessionStorage.getItem('user');
		this.pageOrchestrator.getErrorMessage().hide();
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
							self.pageOchestrator.getErrorMessage().textContent = "Non hai nessuno corso";
							return;
						} 

						self.writeCourses(courseJson);
					}else{
						if(req.status == 403){
							window.location.href="index.html";
							return;
						}
						self.pageOchestrator.getErrorMessage().setError(responseMessage);
						self.pageOchestrator.getErrorMessage().show();
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
				self.reset();
				self.pageOrchestrator.getAppelliList().update(courseTemp, cell);
				self.pageOrchestrator.getAppelliList().show();

			});

			self.container.appendChild(row);
		});
		this.container.style.visibility = "visible";
	}

	this.hide = function(){
		this.pageOrchestrator.getGeneralContainer().innerHTML="";
	}
}