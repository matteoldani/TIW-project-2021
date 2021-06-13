this.courseListGenerator = function(){
	return `<div class="container center" id="courseListContainer">

			

			<h3 id="welcomeTitle"> 
				Benvenuto nella sua Home prof. <span id="docenteName"></span>
				<br>Qui &eacute; possibile scegliere l'appello desiderato e vederne gli iscritti.
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

this.listaIscrittiGenerator = function(){
	return `<!-- lista iscritti appello -->
			<div class="container center justify-content-center" id="iscrittiAppelloContainer">
			

				<h3>
					<span style="color: green;" id="successMessage"></span>
				</h3>

				<h3>
					Corso: <span class="no-bold" id="courseName"> NomeCorso </span>
				</h3>
				<h3>
					Data: <span class="no-bold" id="appelloDate">DataAppello</span>
				</h3>
			
				
				
				<button type="button" class="verbalizzaButton" id="verbalizzaButton" hidden> Verbalizza </button>
				
				<button type="button" class="errorButton" id="homeButton"> Home </button>

				<button type="button" class="errorButton" id="multipleInsertion"> Inserisci Voti </button>
		
			
				<table class="iscritti-table" id="iscrittiAppello">
					<thead class="heading-buttons">
						<tr>
							<th class="iscritti-td sortable">
								Matricola				
							</th>
							<th class="iscritti-td sortable">
								Cognome
							</th>
							<th class="iscritti-td sortable">
								Nome
							</th>
							<th class="iscritti-td sortable">
								E-Mail
							</th>
							<th class="iscritti-td sortable">
								Corso di laurea
							</th>
							<th class="iscritti-td sortable">
								Voto
							</th>
							<th class="iscritti-td sortable">
								Stato valutazione
							</th>
							<th class="pubblica-th">						
								<button type="button" class="" id="pubblicaButton">Pubblica</button>	
							</th>
							
						</tr>
					</thead>

					<tbody id="iscrittiAppelloBody">	
					</tbody>
				</table>

				<!--modal -->
				<div class="modal center" id="modal">
					<div class="modal-content center" id="modal-content">
					
						<table class="modal-table center" id="modal-table">
							<thead class="heading-buttons-modal">
								<tr>
								<th class="iscritti-td sortable">
									Matricola				
								</th>
								<th class="iscritti-td sortable">
									Cognome
								</th>
								<th class="iscritti-td sortable">
									Nome
								</th>
								<th class="iscritti-td sortable">
									E-Mail
								</th>
								<th class="iscritti-td sortable">
									Corso di laurea
								</th>
								<th class="iscritti-td sortable">
									Voto
								</th>
								<th class="iscritti-td sortable">
									Stato valutazione
								</th>
								<th class="pubblica-th">						
									<button type="button" class="green-background" id="pubblicaButton">Inserisci</button>	
								</th>
									
								</tr>
							</thead>
							<tbody id="modal-table-body">
							</tbody>
						</table>
					</div>
				</div>
			</div>`;
}

this.modificaVotoGenerator = function(){
	return `<!-- sezioine modifica/visualizza voto -->
			<div class="container center justify-content-center" id="modificaEsitoContainer">


				<h1>
					<span id="modificaTitle">
					</span>
				</h1>
				
				
				<div  class="center esito" id="modificaVoto">

					<h3> Dati studente </h3>

					<span class="student-info"> Matrciola: </span> <span id="datoMatricola"></span><br>
					<span class="student-info"> Cognome: </span> <span id="datoCognome"></span><br>
					<span class="student-info"> Nome: </span> <span id="datoNome"> </span><br>
					<span class="student-info"> E-Mail: </span> <span id="datoEmail"> </span><br>
					<span class="student-info"> Corso di Laurea: </span><span id="datoCL"></span> <br>
					
					<div id="datoVotoModificabile">
						<form action="#" method="POST" id="formModifica"> <!-- richiesta da fare a modifica voto-->
							<input type="text" id="id_appello_hidden" name="id_appello" hidden>
							<input type="text" id="matricola_hidden" name="matricola" hidden>
							<label  class="student-info">Voto: </label>
							<select name="voto" id="voto">
							  <option value="assente" >Assente</option>
							  <option value="riprovato" >Riprovato</option>
							  <option value="rimandato" >Rimandato</option>
							  <option value="18" >18</option>
							  <option value="19" >19</option>
							  <option value="20" >20</option>
							  <option value="21" >21</option>
							  <option value="22" >22</option>
							  <option value="23" >23</option>
							  <option value="24" >24</option>
							  <option value="25" >25</option>
							  <option value="26" >26</option>
							  <option value="27" >27</option>
							  <option value="28" >28</option>
							  <option value="29" >29</option>
							  <option value="30" >30</option>
							  <option value="lode">30 e Lode</option>
							</select>
							<br>
							<input type="button" value="MODIFICA" class="modificaEsito" id="modificaEsito">
						</form>
					</div>
					
					<div id="datoVotoVisualizzabile">
						<span  class="student-info"> Voto: </span><span id="votoVisualizzabile"></span>
						
					</div>
				</div>

				<button type="button" class="errorButton" id="backToIscritti"> Iscritti Appello </button>
			</div>`;
}

this.verbalizzatiGenerator = function(){
	return `<div class="container center justify-content-center">

			<h1>
				Verbale
			</h1>
		
			
			<div class="verbale">
				<span class="student-info"> Verbale: </span><span id="id_verbale"></span> <br>
				<span class="student-info"> Data: </span> <span id="data_verbale"></span> <br>
				<span class="student-info"> Ora: </span> <span id="ora_verbale"> </span> <br>
				<span class="student-info"> Corso: </span> <span id="corso_verbale"> </span><br>
				<span class="student-info"> Data Appello: </span> <span id="data_appello_verbale"></span><br>
			</div>

			<h1>
				Studenti Verbalizzati
			</h1>
			

				
			<table class="verbalizzati-table" id="container_verbale">
				<thead style="padding: 20px;">
					<th> Matricola </th>
					<th> Voto </th>
				</thead>
				<tbody id="body_verbale">
					
				</tbody>
			</table>
			<button type="button" class="errorButton" id="homeButton"> Home</button>
			<button type="button" class="errorButton" id="iscrittiButton"> Iscritti Appello </button>
		</div>`;
}


this.createModal = function(){
	return `<div class="modal center" id="modal">
				<div class="modal-content center" id="modal-content">
				
					<table class="modal-table center" id="modal-table">
						<thead class="heading-buttons-modal">
							<tr>
							<th class="iscritti-td sortable">
								Matricola				
							</th>
							<th class="iscritti-td sortable">
								Cognome
							</th>
							<th class="iscritti-td sortable">
								Nome
							</th>
							<th class="iscritti-td sortable">
								E-Mail
							</th>
							<th class="iscritti-td sortable">
								Corso di laurea
							</th>
							<th class="iscritti-td sortable">
								Voto
							</th>
							<th class="iscritti-td sortable">
								Stato valutazione
							</th>
							<th class="pubblica-th">						
								<button type="button" class="green-background" id="pubblicaButton">Inserisci</button>	
							</th>
								
							</tr>
						</thead>
						<tbody id="modal-table-body">
						</tbody>
					</table>
				</div>
			</div>`;
}

this.createVotiSelect = function(){
	return `<select name="voto" id="votoOption">
			  <option value="-" > - </option>
			  <option value="assente" >Assente</option>
			  <option value="riprovato" >Riprovato</option>
			  <option value="rimandato" >Rimandato</option>
			  <option value="18" >18</option>
			  <option value="19" >19</option>
			  <option value="20" >20</option>
			  <option value="21" >21</option>
			  <option value="22" >22</option>
			  <option value="23" >23</option>
			  <option value="24" >24</option>
			  <option value="25" >25</option>
			  <option value="26" >26</option>
			  <option value="27" >27</option>
			  <option value="28" >28</option>
			  <option value="29" >29</option>
			  <option value="30" >30</option>
			  <option value="lode">30 e Lode</option>
			</select>`;
}



