# Document downloader tool

This tool helps you download PDF documents from a web page where you would otherwise have to click each document separately. 

## Setup
### 1 Install Chrome Driver to be used by Selenium:
Download from here https://chromedriver.chromium.org/downloads
Unzip to some folder, note the folder. 

Update the main class file with this folder. There is a constant for this. 

### 2 Run Chrome with this command line in order to open the debug port
On mac: 

/Applications/Google\ Chrome.app/Contents/MacOS/Google\ Chrome --remote-debugging-port=9222


### 3 First time you run
 
Perhaps this needs to be done. See for yourself: In Chrome, allow Pupups from the page in question. 

### 4 Chrome auto download PDF
By default, Google Chrome opens a PDF file in the browser window instead of saving it to the downloads folder. To change how Google Chrome treats PDF files in the browser, follow the steps below.

Open the Google Chrome browser.
Click the Menu icon in Google Chrome. icon in the top-right corner of the browser window.
Select Settings from the drop-down menu that appears.
On the Settings window, under Privacy and security, click the Site Settings option.
Scroll down the Site Settings screen to find and click the PDF documents option.
Next to Download PDF files instead of automatically opening them in Chrome, click the toggle switch to set to the On Toggle on switch in Chrome. or Off Toggle off switch in Chrome. position. Chrome downloads a PDF when the toggle switch is set to On and displays a PDF in the browser when set to Off.

### 5 Change Chrome download folder
How to Change the Download Folder in Chrome
Click the menu icon (aka 3 dots) in the upper right corner of the Chrome window.
Select Settings.
Scroll down and click "Show Advanced Settings."
Scroll down to the Downloads section and click Change next to the Download location box.
Select a new folder and click Ok.

### 6 Provide "Downloaded" and "Processed" subfolders
In the folder you configured for download in Chrome, you need to create the following two subfolders:
- DOWNLOADED
- PROCESSED

### 7 Change the 3 TODOs that define how the page is made
Locate 3 todos in the code that configure strings. These strings define how the page is structured: The url of the page with documents, 
the button (if any) to load more documents, and the subelement on the page that contains the list of the documents. 

### Last step
In the instance of Chrome that you launched, login and run the main class of this project.
Remember that the documents will appear with names in the language that is displayed. To change the language, change the 
language of the displayed page (in the disconnect button).

### Launch
Launch the application with 3 arguments: 
- Chrome download folder
- From date
- To date

E.g.
/Users/myhappyuser/Downloads/Chrome 01/01/2021 01/01/2022
