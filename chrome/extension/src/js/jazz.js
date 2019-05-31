try {
    if (!Jazz) {
        throw new Error('Jazz object is not defined.')
    }
} catch (err) {
    displayError(err);
}


window.onload = function () {
        // Verify the user with the access token and get the auth user's profile 
        chrome.identity.getAuthToken({
            interactive: true
        }, function(token) {
            if (chrome.runtime.lastError) {
                displayError(chrome.runtime.lastError.message);
                return;
            }
            var xhr = new XMLHttpRequest();
            xhr.open('GET', 'https://www.googleapis.com/oauth2/v2/userinfo?alt=json&access_token=' + token);
            xhr.onload = function() {
                user_profile = JSON.parse(xhr.responseText);
                
                let params =
                'groups=' + Jazz.group +
                '&name=' + user_profile.name +
                '&email=' + user_profile.email+
                '&token=' + token;

                let url = 'https://secure.livechatinc.com/licence/' + Jazz.license + '/open_chat.cgi?' + params;
                let frame = document.getElementById('jazz-content');
                frame.src = url;
            };
            xhr.send();
        });
}


/////// Functions ////////

function displayError(error) {
    // Add Jazz error message
    console.log("Error has happened. Error = " + error);
    let errorText = document.createTextNode(error);
    let errorMessage = document.createElement("div");
    errorMessage.id = "not_signed_in";
    errorMessage.appendChild(errorText);
    errorMessage.style.cssText = "padding:5px;position:relative;width:100%;min-width:300px;z-index:100;text-align:center;font-size: x-large;";
    document.body.appendChild(errorMessage);

    // Add Jazz image
    let errorImage = document.createElement("IMG");
    errorImage.src = "../../img/jazz-64.png";
    errorImage.id = "error_image";
    errorImage.style.cssText = "padding:5px;display:block;margin:auto;";
    document.body.appendChild(errorImage);
}
