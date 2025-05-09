BACKEND USER STORIES & RULES

🧑‍💼 General Users

User Registration & Login

Implement secure registration and login using email/password.

Use JWT for authentication.

Reset Password

Add endpoint to trigger password reset.

Profile Management

Expose endpoints to get and update profile (firstName, lastName, age, profession, phoneNumber, region).

Points System

Track totalPoints per user.

Allow conversion of points to gifts.

User Level System

Implement Level entity tied to pointsThreshold.

Assign user level based on accumulated points.

🧑‍🎓 Participants

View & Participate in Surveys

Endpoint to get active surveys.

Save participant answers via ResParticipant.

Survey Answering

Support CHOICE and TEXT types.

Save selected answers in ResParticipant.

Track Reward Points

On survey submission, update totalPoints.

Answer Review

Enable participants to view their previous responses.

🧑‍💻 Creators

Create Survey

Endpoint to create a survey with title, description, rewardPoints, forum toggle.

Edit & Delete Surveys

Allow update and deletion of surveys.

Limit Participants

Enforce maxParticipants value.

🗣️ Forum Management

Add Forum to Survey

Enable forums tied to specific surveys.

Post Sujet

Endpoint to add sujet (topic) in a forum.

Post Commentaire

Allow users to add comments to sujets.

🛡️ Admin Users

Ban Users

Admin can ban any user via endpoint.

Verify Surveys

Endpoint to mark surveys as verified.

🎖️ Gamification

Badges

Create and assign badges based on user achievements.

Gifts

Store available gifts with pointsCost.

Allow users to redeem them.