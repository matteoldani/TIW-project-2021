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