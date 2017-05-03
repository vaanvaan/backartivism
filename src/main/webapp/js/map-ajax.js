$(document).ready(function() {
	console.log('ready');
	$('#tweetquery').blur(function() {
		$.ajax({
			url : 'GetTweetsServlet',
			data : {
				tweetquery : $('#tweetquery').val()
			},
			success : function(responseText) {
				console.log(responseText);
				var jsonData = JSON.parse(responseText);
				var locations = [];
				$.each(jsonData, function( index, value ) {
					  var marker = new Array();
					 console.log(value);
					 marker[0] = value.text;
					 marker[1] = value.latitude;
					 marker[2] = value.longitude;
					 locations[index] = marker;
				});
				
				    //['Maroubra Beach', -33.950198, 151.259302, 1]
	
				  var map = new google.maps.Map(document.getElementById('map'), {
				    zoom: 10,
				    center: new google.maps.LatLng(40.416775, -3.703790),
				    mapTypeId: google.maps.MapTypeId.ROADMAP
				  });
	
				  var infowindow = new google.maps.InfoWindow();
				  
				  google.maps.event.addListener(infowindow, 'domready', function () {
				        ! function (d, s, id) {
				            var js, fjs = d.getElementsByTagName(s)[0];
				            if (!d.getElementById(id)) {
				                js = d.createElement(s);
				                js.id = id;
				                js.src = "//platform.twitter.com/widgets.js";
				                fjs.parentNode.insertBefore(js, fjs);
				            }
				        }(document, "script", "twitter-wjs");
				   });
				  
				  var marker, i;
	
				  for (i = 0; i < locations.length; i++) {  
				    marker = new google.maps.Marker({
				      position: new google.maps.LatLng(locations[i][1], locations[i][2]),
				      map: map
				    });
	
				    google.maps.event.addListener(marker, 'click', (function(marker, i) {
				      return function() {
				        infowindow.setContent(locations[i][0]);
				        infowindow.open(map, marker);
				      }
				    })(marker, i));
				  }
			}
		});
	});
});