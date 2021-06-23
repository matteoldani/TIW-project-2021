( function() {


	/*
	* L'oggetto page orchestrator viene composto da una funzione che ritorna un oggetto con varie funzioni
	* in questo modo è possibile creare dei getter che mi permettano di accedere agli attributi della pagina
	* anche dagli altri componenti della stessa (getter) ed è possibile manterenere una funzione di start
	*/

	var pageOrchestrator = (function PageOrchestratorCreator(){
		var courseList = null,
			appelliList = null,
			listaIscritti = null,
			modificaVoto = null,
			errorMessage = null,
			verbalizzaVoto = null,
			generalContainer = document.getElementById("generalContainer");


		return {
			start: function(_pageOrchestrator){

				errorMessage = new ErrorMessage(_pageOrchestrator);
				courseList = new CourseList(_pageOrchestrator);
				appelliList = new AppelliList(_pageOrchestrator);
				listaIscritti = new ListaIscritti(_pageOrchestrator);
				modificaVoto = new ModificaVoto(_pageOrchestrator);
				verbalizzaVoto = new VerbalizzaVoto(_pageOrchestrator);

				//creo oggetto capace di tenere i corsi al suo interno
				courseList.update();

				document.querySelector("a[href='/verbalizzazione_voti_js/Logout']").addEventListener('click', () => {
		      	  		window.sessionStorage.removeItem('user');
		      	  		window.location.href="/verbalizzazione_voti_js/index.html";
		      		}
		      	);

		      	courseList.show();

		    },

		    getCourseList : function(){
		    	return courseList;
		    },

		    getAppelliList : function(){
		    	return appelliList;
		    },

		    getListaIscritti : function(){
		    	return listaIscritti;
		    },

		    getModificaVoto : function(){
		    	return modificaVoto;
		    },

		    getErrorMessage : function(){
		    	return errorMessage;
		    },

		    getVerbalizzaVoto : function(){
		    	return verbalizzaVoto;
		    },

		    getGeneralContainer : function(){
		    	return generalContainer;
		    },

		};
	})();

    window.addEventListener("load", () => {
      if (sessionStorage.getItem("user") == null) {
        window.location.href = "index.html";
      } else {
        pageOrchestrator.start(pageOrchestrator); // initialize the components
      } // display initial content
    }, false);

})();
