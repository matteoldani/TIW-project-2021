//componente per la lista degli appelli posssibili per il corso selezionato
	function AppelliList(_pageOrchestrator){

		this.pageOrchestrator = _pageOrchestrator
		//container e body andranno creati quando viene lanciato l'evento e sanno parte di course List
		this.course;
		this.container;
		this.appelli;

		this.update = function(_course, _container){
			//non vado a reinizializzare il contenuto del container genetale perchè questa sezione fa parte di 
			//un unico componente html
			this.course = _course;
			this.container = _container;
		}

		this.show = function(){

			this.pageOrchestrator.getErrorMessage().hide();
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
								self.pageOrchestrator.getErrorMessage().setError("Non hai nessuno appello per questo corso");
								self.pageOrchestrator.getErrorMessage().show();
								return;
							}

							self.writeAppelli(appelliJson);
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
					event.stopPropagation();
					self.hide()
					self.pageOrchestrator.getCourseList().hide();
					self.pageOrchestrator.getListaIscritti().update(appelloTemp.getId(), self.course.getName(), appello.data);
					self.pageOrchestrator.getListaIscritti().show();
				});

			});

			this.container.appendChild(ul);

		}

		this.hide = function(){
			//non elimino l'inner html perchè è legato al course list
			this.container.setAttribute("hidden", "true");
		}
	}