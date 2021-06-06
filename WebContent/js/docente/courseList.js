
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