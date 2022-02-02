//componente per la gestione della visualizzazione del verbale
	//e della chiamata per la verbalizzazione dei voti
	this.pageOrchestrator;

	function VerbalizzaVoto(_pageOrchestrator){
		pageOrchestrator = _pageOrchestrator;
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
			pageOrchestrator.getGeneralContainer().innerHTML = "";
			pageOrchestrator.getGeneralContainer().innerHTML = verbalizzatiGenerator();

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
				pageOrchestrator.getCourseList().update();
				pageOrchestrator.getCourseList().show();
			});

			//se schiaccio il bottone per toare alla pagina degli iscitti
			this.iscrtittiAppello.addEventListener("click", (event) => {
				this.hide();
				pageOrchestrator.getListaIscritti().update(this.id_appello, this.corso.textContent, this.dataAppello.textContent);
				pageOrchestrator.getListaIscritti().show();
			});

		}

		this.show = function(){
			pageOrchestrator.getErrorMessage().hide();
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
							pageOrchestrator.getErrorMessage().setError(responseMessage);
							pageOrchestrator.getErrorMessage().show();
						}
					}

				});
		}

		this.writeVerbalizzati = function(verbalizzatiJson){


			this.verbale.textContent = verbalizzatiJson.id_verbale;
			//creo la data
			this.data.textContent = verbalizzatiJson.data.day + "/" + verbalizzatiJson.data.month + "/" + verbalizzatiJson.data.year;
			//creo l'ora 
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
			pageOrchestrator.getGeneralContainer().innerHTML = "";
		}
	}