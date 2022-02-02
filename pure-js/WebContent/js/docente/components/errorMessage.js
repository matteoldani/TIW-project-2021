this.pageOrchestrator;

//componente che raccoglie i messaggi di errore
	function ErrorMessage(_pageOrchestrator){
		pageOrchestrator = _pageOrchestrator;
		
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