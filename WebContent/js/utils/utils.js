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


// TABLE SORTING MANAGEMENT FUNCTIONS

/*
 * Self invoking unnamed function. This generates a scope around the code which
 * causes variables and functions not to end up in the global scope.
 */

this.makeElementSortable = function() {

  this.lastClickedColumn = document.querySelector('th.sortable');
  this.asc = false;

  // Returns the text content of a cell.
  function getCellValue(tr, idx) {
    return tr.children[idx].textContent; // idx indexes the columns of the tr
    // row
  }

  /*
   * Creates a function that compares two rows based on the cell in the idx
   * position.
   */
  function createComparer(idx, asc) {
    console.log(asc)
    return function(a, b) {
      // get values to compare at column idx
      // if order is ascending, compare 1st row to 2nd , otherwise 2nd to 1st
      var v1 = getCellValue(asc ? a : b, idx),
        v2 = getCellValue(asc ? b : a, idx);
      // If non numeric value
      if (v1 === '' || v2 === '' || isNaN(v1) || isNaN(v2)) {
        return v1.toString().localeCompare(v2); // lexical comparison
      }
      // If numeric value
      return v1 - v2; // v1 greater than v2 --> true
    };
  }

  // For all table headers f class sortable
  document.querySelectorAll('th.sortable').forEach(function(th) {
    
    //var asc = true;
    // Add a listener on the click event
    var self = this;
    th.addEventListener('click', function () {
      if(self.lastClickedColumn != th){
        self.asc = false;
        self.lastClickedColumn = th;
        console.log(self.lastClickedColumn);
      }
      var table = th.closest('table'); // get the closest table tag
      // For every row in the table body
      // Use Array.from to build an array from table.querySelectorAll result
      // which is an Array Like Object (see DOM specifications)
      Array.from(table.querySelectorAll('tbody > tr'))
        // Toggle the criterion and to sort rows with the comparator function
        // passing
        // (index of column to compare, sort criterion asc or desc) --this is
        // the the
        // element
        .sort(createComparer(Array.from(th.parentNode.children).indexOf(th), self.asc = !self.asc))
        // Append the sorted rows in the table body
        .forEach(function(tr) {
          table.querySelector('tbody').appendChild(tr)
        });
    });
  });
}



