# Random Ayah Generator

An Android application that allows users to bookmark and generate random Quranic verses (Ayahs). It features a built-in Quran database, bookmarking functionality, playback count tracking, and a dashboard for viewing statistics. The app supports both light and dark modes.

## Features

### Random ayah generation

Generates random pairs of Ayahs from the bookmarked Ayahs.  
<img src="screenshots/random_ayah_home_light.jpg" alt="Random Ayah Generation (Light)" width="250"/> <img src="screenshots/random_ayah_home_dark.jpg" alt="Random Ayah Generation (Dark)" width="250"/>
<img src="screenshots/generated_pair_light.jpg" alt="Random Ayah Generation (Light)" width="250"/> <img src="screenshots/generated_pair_dark.jpg" alt="Random Ayah Generation (Dark)" width="250"/>

### Bookmark ayahs

Allows users to manually add and bookmark Ayahs, either individually or entire Surahs.  
<img src="screenshots/add_ayah_dialog_light.jpg" alt="Add Ayah Dialog (Light)" width="250"/> <img src="screenshots/add_ayah_dialog_dark.jpg" alt="Add Ayah Dialog (Dark)" width="250"/>

### Add complete surah

Easily bookmark all Ayahs from a selected Surah.  
<img src="screenshots/add_complete_surah_light.jpg" alt="Add Complete Surah Checkbox (Light)" width="250"/> <img src="screenshots/add_complete_surah_dark.jpg" alt="Add Complete Surah Checkbox (Dark)" width="250"/>

### View bookmarked ayahs

Scrollable list with infinite scrolling to view all bookmarked Ayahs.  
<img src="screenshots/view_data_light.jpg" alt="View Bookmarked Data (Light)" width="250"/> <img src="screenshots/view_data_dark.jpg" alt="View Bookmarked Data (Dark)" width="250"/>

### Delete bookmarked ayahs

Delete individual Ayahs from bookmarks with confirmation.  
<img src="screenshots/delete_ayah_light.jpg" alt="Delete confirmation (Light)" width="250"/> <img src="screenshots/delete_ayah_dark.jpg" alt="Delete confirmation (Dark)" width="250"/>

### Dashboard

Displays statistics like the most played Ayahs, most played Surahs, and total play count.  
<img src="screenshots/dashboard_light_1.jpg" alt="Dashboard (Light)" width="250"/> <img src="screenshots/dashboard_dark_1.jpg" alt="Dashboard (Dark)" width="250"/>
<img src="screenshots/dashboard_light_2.jpg" alt="Dashboard (Light)" width="250"/> <img src="screenshots/dashboard_dark_2.jpg" alt="Dashboard (Dark)" width="250"/>

### 📂 Navigation drawer

Facilitates smooth navigation between different activities.  
<img src="screenshots/navigation_drawer_light.jpg" alt="Navigation Drawer (Light)" width="250"/> <img src="screenshots/navigation_drawer_dark.jpg" alt="Navigation Drawer (Dark)" width="250"/>

### 🗄️ Database management

- **Save Database:** Export the bookmarked Ayahs database to a file.  
- **Load Database:** Import a previously saved database, replacing the current bookmarks.  
- **Delete All Data:** Clear all bookmarked Ayahs.

## ⚙️ Setup and usage

1. **Clone the repository:**

   ```bash
   git clone git@github.com:ImadSaddik/RandomAyahGenerator.git
   ```

2. **Open the project in Android Studio.**

3. **Build and run the application** on an emulator or physical device.

## 📚 Key concepts

I learned a lot while working on this project. First, I made wireframes for each part of the app before writing any code. This helped me see how the app would look and made it easier to build in Android Studio.  

From the start, I designed the app to support both light and dark modes. When I switched themes, I also figured out how to use state management to reload data that got lost during the switch.  

I learned how to manage SQLite databases, create navigation drawers, and use infinite scrolling to go through the stored data smoothly.  
