//lista di corsi nella pagina iniziale con predisposizione per gli appelli
this.courseListGenerator = function(){
	return `<div class="container center" id="courseListContainer">

			

			<h3 id="welcomeTitle"> 
				Benvenuto nella sua Home <span id="studenteName"></span>
				<br>Qui &eacute; possibile scegliere l'appello desiderato e vederne l'esito.
			</h3>
			
		
			<table class="center" id="courseList">
				<thead>
					<tr>
						<th class="table-title">Corsi</th>
					</tr>
				</thead>

				<tbody id="courseListBody">
				</tbody>	

			</table>
		</div>`;
}

//form per la visualizzazione dell'esito dell'esame con bottone per rifiutarlo
this.createEsitoEsame = function(){
	return `<div class="container center justify-content-center">
			

			<h1> Esito Esame </h1>

					
			<div class="center esito" id="container_esito_esame">
				<h3>
					Corso: <span class="no-bold" id="corso"></span>
				</h3>
				<h3>
					Data: <span class="no-bold" id="data"></span>
				</h3>
				
				<div id="body_esito_esame">
					<h3> Dati studente </h3>
					<span class="student-info"> Matrciola: </span> <span id="matricola"></span><br>
					<span class="student-info"> Cognome: </span> <span id="cognome"></span> <br>
					<span class="student-info"> Nome: </span> <span id="nome"> Matteo</span><br>
					<span class="student-info"> E-Mail: </span> <span id="email"> matteo.oldai@mail.polimi.it</span><br>
					<span class="student-info"> Corso di Laurea: </span><span id="corsoLaurea"></span> <br>
					<span class="student-info"> Voto: <span id="voto"></span></span><br>
					<button type="button" class="rifiutaButton" id="rifiutaButton"> Rifiuta </button>
					
					<br>
					<span id="votoRifiutato" class="votoRifiutato"></span> 
				
				</div>
			</div>

			<button type="button" class="errorButton" id="homeButton"> Home </button>
		</div>`;
}

