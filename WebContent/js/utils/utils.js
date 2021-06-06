/**
 * generic function ablo to handle request and response.
 * it requires the method (POST; GET), the url, possible form, callback function and what to do if
 * a form is submitted (rest or not)
 */

function makeCall(method, url, formElement, cback, reset = true) {
	//creates the actual request
    var req = new XMLHttpRequest(); // visible by closure

    //set the callback function to be called when the server send a response
    req.onreadystatechange = function() {
      cback(req)
    }; // closure

    //prepare the sequest with the form if needed
    req.open(method, url);
    if (formElement == null) {
      req.send();
    } else {
        var formData = new FormData(formElement);
        console.log(formData);
        req.send(formData);
    }

    if (formElement !== null && reset === true) {
      formElement.reset();
    }
  }
