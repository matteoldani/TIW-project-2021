( function(){

	
	
	var pageOrchestrator = (function() {
		var courseList,
			appelliList,
			errorMessage,
			esitoEsame,
			generalContainer = document.getElementById("generalContainer");

		return {
			start: function(_pageOrchestrator) {
				errorMessage = new ErrorMessage(_pageOrchestrator);
				courseList = new CourseList(_pageOrchestrator);
				appelliList = new AppelliList(_pageOrchestrator);
				esitoEsame = new EsitoEsame(_pageOrchestrator);

				//richiedo i corsi a cui sono iscritto e faccio vedere la prima parte di home
				courseList.update();

				//registro l'evento di logout
				document.querySelector("a[href='/verbalizzazione_voti_js/Logout']").addEventListener('click', () => {
		      	  		window.sessionStorage.removeItem('user');
		      	  		window.location.href="/verbalizzazione_voti_js/index.html";
	      			}
	      		);

      			courseList.show();
			},

			getCourseList: function(){
				return courseList;
			},

			getAppelliList: function(){
				return appelliList;
			},

			getEsitoEsame: function(){
				return esitoEsame;
			},

			getErrorMessage: function(){
				return errorMessage;
			},

			getGeneralContainer: function(){
				return generalContainer;
			}
		};
	})();

	window.addEventListener("load", () =>{
		if(sessionStorage.getItem("user") == null){
			window.location.href = "index.html";
		}else{
			pageOrchestrator.start(pageOrchestrator);
		}
	}, false);
})();



