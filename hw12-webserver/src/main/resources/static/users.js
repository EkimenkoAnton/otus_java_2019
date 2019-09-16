$(document).ready(function(){

	$( "#add_user_btn" ).click(function() {
		let clickedButton = $( this );

		let $form =clickedButton.closest('form')
			url = $form.attr( "action" );

		sendedData = getInputsFromForm($form);

		sendPostRequest(url, sendedData, clickedButton);
	});


	$( "#get_users_btn" ).click(function() {

		let clickedButton = $( this );

		btnName=getButtonName(clickedButton);

		freezeButton(clickedButton);
		setButtonName(clickedButton, "wait...");

		let req = $.get("http://localhost:8080/getUsers");
		req.done(function( data ) {
				$( "#dataHolder" ).empty().append( jsonToTable(data) );
		});
		req.fail(function() { alert("error"); });

		req.always(function() {
			unFreezeButton(clickedButton);
			setButtonName(clickedButton, btnName);
		});
	});


	$("#add_phone_btn").click(function () {
		$("<input type='text' class='input_field' name='phone[]' placeholder='phone' title='phone' size='15'>").insertBefore("#add_phone_btn");
	})


	function sendPostRequest(url, sendedData, freezingButton) {

		btnName=getButtonName(freezingButton);

		freezeButton(freezingButton);
		setButtonName(freezingButton, "wait...");

		let posting = $.post( url, sendedData );

		posting.done(function( data ) {
				alert('User added');
			});

		posting.fail(function() { alert("Fail to add user"); });

		posting.always(function() {
			unFreezeButton(freezingButton);
			setButtonName(freezingButton, btnName);
		});
		}

	function freezeButton(btn){
		btn.attr('disabled', true);
	}

	function unFreezeButton(btn){
		btn.attr('disabled', false);
	}


	function setButtonName(btn, newName){
		btn.attr('value', newName);
	}

	function getButtonName(btn){
		return btn.attr('value');
	}

	function getInputsFromForm(currForm) {
		let retVal = new Object;
		currForm.find("input[type='text']").each(function(){
			let name = $(this).attr('name');
			let value = $(this).val();
			if(name.endsWith("[]")) {
				if (value!=='') {
				let arr = [];
				if(typeof retVal[name] == "undefined") {
					retVal[name] = arr;
				}
				arr = retVal[name];
				arr.push(value);
				retVal[name] = arr;
			}
			} else {
				retVal[name] = value;
			}
		});

		//alert(JSON.stringify(retVal));
		return retVal;
	}

	function jsonToTable(jsonData) {
		let strBuilder="";
		strBuilder=strBuilder+
		'<table border="1"><caption>List of users</caption>'+
		'<tr>'+
			'<th>id</th>'+
			'<th>name</th>'+
			'<th>age</th>'+
			'<th>adress</th>';
			let maxPhoneCnt=0;
			for (let i = 0; i < jsonData.length; i++) {
				let tmpPhoneCnt = 0;
			    for (let key in jsonData[i]) {
			    	let val=jsonData[i][key];
			    	if (Array.isArray(val)) {
			    		for (let k = 0; k < val.length; k++) {
			    			tmpPhoneCnt++;
			    			if (tmpPhoneCnt>maxPhoneCnt)
			    				strBuilder=strBuilder+'<th>phone</th>';
			    		}
			    	}
			    }
			    if (tmpPhoneCnt>maxPhoneCnt)
			    	maxPhoneCnt = tmpPhoneCnt;
			}
			strBuilder=strBuilder+'</tr>';

		for (let i = 0; i < jsonData.length; i++) {
			strBuilder=strBuilder+'<tr>';
            for (let key in jsonData[i]) {
            	let val=jsonData[i][key];
            	if (Array.isArray(val)) {
            		for (let k = 0; k < val.length; k++) {
            			for (let innerKey in val[k]) {
            				if(innerKey!=='id') {
            					strBuilder=strBuilder+'<td>'+val[k][innerKey]+'</td>';
            				}
            			}
            		}
            	} else if (typeof val === "object") {
            		for (let innerKey in val) {
            			if(innerKey!=='id') {
            				strBuilder=strBuilder+'<td>'+val[innerKey]+'</td>';
            			}
            		}
            	} else {
            		strBuilder=strBuilder+'<td>'+val+'</td>';
            	}
            }
            strBuilder=strBuilder+'</tr>';
        }

        strBuilder=strBuilder+'</table>'
        return strBuilder;
	}


});