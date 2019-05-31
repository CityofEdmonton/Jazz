import React from 'react';
import logo from './logo.svg';
import './App.css';
import { GoogleLogin } from "react-google-login";
import { Offline, Online } from "react-detect-offline";

function App() {

  const isIos = () => {
    const userAgent = window.navigator.userAgent.toLowerCase();
    return /iphone|ipad|ipod/.test( userAgent );
  }
  // Detects if device is in standalone mode
  const isInStandaloneMode = () => ('standalone' in window.navigator) && (window.navigator.standalone);
  
  // Checks if should display install popup notification:
  if (isIos() && !isInStandaloneMode()) {
    alert("To put a Jazz shortcut on your Home screen:\n1) Tap the Share button in Safari.\n2) Tap 'Add to Home Screen'.\n3) Tap 'Add' in the upper-right corner.");
  }

  let firstTime = localStorage.getItem('firstTime');
  let isLoggedIn = false;
  if (firstTime != null){
    isLoggedIn = true;
  }

  if (isLoggedIn){
    return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <p></p>
          <Online>
          <span role="img" aria-label="rocket">🚀</span> Lunching Jazz Chat window...
          <p></p>
          <GoogleLogin
          clientId={process.env.REACT_APP_clientId}
          scope="https://www.googleapis.com/auth/userinfo.email"
          buttonText="Please click here if it's not loading"
          onSuccess={response => onloggedIn(response)}
          onFailure={response => console.log("onFailure", response)}
          onRequest={response => console.log("onRequest", response)}
          isSignedIn={true}
          icon={false}
          theme="dark"
          />
          </Online>
  
        <Offline><span role="img" aria-label="link">🔗 </span> Please check your internet connection!</Offline>
        </header>
      </div>
      );
  }
  
  else if (isLoggedIn === false)
  {
    return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p></p>
        <Online>
        <GoogleLogin
        clientId={process.env.REACT_APP_clientId}
        scope="https://www.googleapis.com/auth/userinfo.email"
        buttonText="Please Login with COE Gmail"
        onSuccess={response => onloggedIn(response)}
        onFailure={response => console.log("onFailure", response)}
        onRequest={response => console.log("onRequest", response)}
        isSignedIn={true}
        icon={false}
        theme="dark"
      />
      </Online>

      <Offline><span role="img" aria-label="link">🔗 </span> Please check your internet connection!</Offline>
      </header>
    </div>
    );
  }
}

function onloggedIn(response){
  let firstTime = localStorage.getItem('firstTime');
  if (firstTime === null){
    localStorage.setItem('firstTime',  false);
  }
  initChat(response);
}

function initChat(userInfo){
  let params =
  'groups=' + process.env.REACT_APP_groups+
  '&name=' + userInfo.profileObj.name +
  '&email=' + userInfo.profileObj.email+
  '&token=' + userInfo.Zi.access_token;

  let url = 'https://secure.livechatinc.com/licence/'+process.env.REACT_APP_licence+'/open_chat.cgi?' + params;
  window.location.href = url;
}

export default App;
