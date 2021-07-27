Original App Design Project - README Template
===

# SimpleLeague

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
SimpleLeague is an application centered primarily around the popular MMO, League of Legends. In this application, users can view a wide variety of information about the game such as champions, abilities, lores and objectives. The users of this application also have the option of creating an account to view posts by other users and create their own. This application aims to create a community around this popular MMO that can be accessed on the go, at home, or wherever there is internet and a mobile device.


### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Social / Entertainment
- **Mobile:** This app can be accessed wherever their is a mobile device and internet. In the future, users can use camera feature of their mobile devices to upload images to the app.
- **Story:** The app's value is mostly dependent on the users and the communities and relationships they build with other users within the app.
- I believe my friends and peers would like the idea as it takes in several app types: wiki, social media, forum and combines them into one for a specific MMO community.
- **Market:** The whole league of legends community which consists of millions of players.
- **Habit:** Daily as they can view newly created posts by other users and comment on those posts.
- **Scope:** It will be technically challenging to complete all the features including the optional ones by the end of the program. The product is pretty much a wikipedia for the game League of Legends with a forum where users can post questions, concerns, etc.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can view a list of champions in the game
* User can expose champion details
* User can create a new account
* User can login
* User can create a post or comment on a post
* User can view a feed of most recent posts to app
* User can search for posts by a certain user
* User can like posts
* User can logout

**Optional Nice-to-have Stories**

* User can follow other users
* User can view a feed of posts of their own and users they follow (hometimeline)
* User can view profile page with number of posts, followers and following
* User can upload custom profile image and change display name
* User can view a discover page to find posts by different creators

### 2. Screen Archetypes

* homepage screen
    * button to view list of champions
    * button to view list of objctives
    * button to go to login page
* champions screen
    * view list of champions (each in CardActivity)
    * search icon for a specific champion
    * click champion to expose details
* champion details screen
    * view champion specific details (abilities, lore)
* objectives screen
    * view list of objectives / neutral monsters
    * search icon for a specific objective
    * click objective to expose details
* objective details screen
    * view objective specific details
* login screen
    * user can log in via gmail
    * after login go to homepage for logged in user
* logged in home page screen
    * icon to create posts
    * icon to search posts
    * icon to view feed
* create posts screen
    * user can create post with title and body
    * verifies post and publishes to database
* search posts/users screen
    * search for a user and view posts by them
* view feed/discover
    * user can view feed of posts of their own and users they follow (hometimeline)
    * user can view discover feed and view most recent posts
* post details screen
    * user can view the full details of a post
 
### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Champions Screen
* Objectives Screen
* Login Screen

**Flow Navigation** (Screen to Screen)

* homepage screen
    * champions screen
    * objectives screen
    * login screen
* champions screen
    * expose champion screen
* objectives screen
    * expose objective screen
* login screen
    * logged in homepage screen
* logged in homepage screen
    * create posts screen
    * search posts screen
    * view feed screen
* create posts screen
    * view feed screen after post has been published
* view feed screen
    * post details screen


## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="https://user-images.githubusercontent.com/66713616/124811282-4db40600-df30-11eb-82f2-7fd70a27eb71.png" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models
#### User
Property|Type|Description
---|---|---
objectID|String|unique id for the user post (default field)
username|String|name of user
password|String|password for user
profileImage|File|profile image for user
biography|String|custom bio for user
followers|Array|array of users that follow current user
following|Array|array of users that current user follows
createdAt|DateTime|date when post is created (default field)
updatedAt|DateTime|	date when post is last updated (default field)

#### Post
Property|Type|Description
---|---|---
objectID|String|unique id for the user post (default field)
author|Pointer to User|image author
image|File|image that user posts
comments|Array|array of comments to post
upvotesCount|Number|number of upvotes on post
createdAt|DateTime|date when post is created (default field)
updatedAt|DateTime|	date when post is last updated (default field)

#### Comment
Property|Type|Description
---|---|---
objectID|String|unique id for the user comment (default field)
author|Pointer to User|image author
replies|Array|array of replies(comments) to comment
upvotesCount|Number|number of upvotes on comment
createdAt|DateTime|date when post is created (default field)
updatedAt|DateTime|date when post is last updated (default field)

### Champion
Property|Type|Description
---|---|---
objectID|String|unique id for the champion (default field)
name|String|name of champion
image|File|image of champion
lore|String|champion's lore/story
abilities|Pointer to ChampionAbilities|champion's abilities

#### ChampionAbilities
Property|Type|Description
---|---|---
objectID|String|unique id for the champion (default field)
name|String|name of champion
abilityQ|String|name of champion's Q ability
abilityQImage|File|image of champion's Q ability
abilityW|String|name of champion's W ability
abilityWImage|File|image of champion's W ability
abilityE|String|name of champion's E ability
abilityEImage|File|image of champion's E ability
abilityR|String|name of champion's R ability
abilityRImage|File|image of champion's R ability
abilityP|String|name of champion's P ability
abilityPImage|File|image of champion's P ability


#### Objectives (Optional)
Property|Type|Description
---|---|---
objectID|String|unique id for the champion (default field)
name|String|name of objective
image|File|image of objective
description|String|descrition of objective
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
