$(document).ready(function() {		$('#leadsForm').validate({	    rules: {	      building_name: {	        minlength: 5,	        required: true	      },	      status: {	        required: true	      }	    },		highlight: function(element) {		    $(element).closest('.control-group').removeClass('success').addClass('error');		},		success: function(element) {		    element.text('OK!').addClass('valid').closest('.control-group').removeClass('error').addClass('success');		}	});});