package org.example.gamified_survey_app.survey.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gamified_survey_app.auth.model.AppUser;
import org.example.gamified_survey_app.auth.repository.UserRepository;
import org.example.gamified_survey_app.core.constants.Roles;
import org.example.gamified_survey_app.survey.model.*;
import org.example.gamified_survey_app.survey.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Component
@Order(4) // Execute after user and category seeders
@RequiredArgsConstructor
@Slf4j
public class SurveyDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final ForumRepository forumRepository;
    private final SubjectRepository subjectRepository;
    private final CommentRepository commentRepository;
    private final SurveyResponseRepository surveyResponseRepository;
    private final QuestionResponseRepository questionResponseRepository;

    private final Random random = new Random();

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (surveyRepository.count() == 0) {
            log.info("Starting survey data seeding...");
            seedSurveysWithForumsAndResponses();
            log.info("Survey data seeding completed successfully!");
        } else {
            log.info("Survey data already exists, skipping seeding.");
        }
    }

    private void seedSurveysWithForumsAndResponses() {
        // Get users by roles
        List<AppUser> creators = userRepository.findByRole(Roles.CREATOR);
        List<AppUser> participants = userRepository.findByRole(Roles.PARTICIPANT);
        List<Category> categories = categoryRepository.findAll();

        if (creators.isEmpty() || participants.isEmpty() || categories.isEmpty()) {
            log.warn("Not enough data to seed surveys. Need creators, participants, and categories.");
            return;
        }

        // Create surveys for each creator
        for (AppUser creator : creators) {
            int surveysPerCreator = random.nextInt(3) + 2; // 2-4 surveys per creator

            for (int i = 0; i < surveysPerCreator; i++) {
                Survey survey = createSurvey(creator, categories);
                Survey savedSurvey = surveyRepository.save(survey);

                // Create questions for the survey
                createQuestionsForSurvey(savedSurvey);

                // Create forum if enabled
                if (savedSurvey.isHasForum()) {
                    Forum forum = createForumForSurvey(savedSurvey);
                    createForumContent(forum, participants);
                }

                // Create survey responses from participants
                createSurveyResponses(savedSurvey, participants);

                log.info("Created survey: {} with forum: {}", savedSurvey.getTitle(), savedSurvey.isHasForum());
            }
        }
    }

    private Survey createSurvey(AppUser creator, List<Category> categories) {
        String[] titles = {
                "Student Learning Preferences Survey",
                "Technology Usage in Education",
                "Mental Health and Wellbeing Study",
                "Consumer Behavior Analysis",
                "Environmental Awareness Survey",
                "Social Media Impact Research",
                "Work-Life Balance Study",
                "Food Preferences and Dietary Habits",
                "Transportation and Mobility Survey",
                "Digital Privacy Concerns Study"
        };

        String[] descriptions = {
                "Help us understand how students prefer to learn and what methods work best for them.",
                "Explore the impact of technology on modern education and learning outcomes.",
                "Research on mental health awareness and support systems in academic environments.",
                "Analyze consumer behavior patterns and purchasing decisions in the digital age.",
                "Study environmental consciousness and sustainable practices among young adults.",
                "Investigate the effects of social media usage on daily life and relationships.",
                "Examine work-life balance challenges and solutions in the modern workplace.",
                "Research dietary preferences, eating habits, and nutrition awareness.",
                "Study transportation preferences and mobility patterns in urban areas.",
                "Explore concerns and awareness about digital privacy and data protection."
        };

        Survey survey = new Survey();
        survey.setTitle(titles[random.nextInt(titles.length)]);
        survey.setDescription(descriptions[random.nextInt(descriptions.length)]);
        survey.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
        survey.setExpiresAt(LocalDateTime.now().plusDays(random.nextInt(60) + 30));
        survey.setCreator(creator);
        survey.setCategory(categories.get(random.nextInt(categories.size())));
        survey.setActive(true);
        survey.setVerified(random.nextBoolean());
        survey.setHasForum(random.nextBoolean());
        survey.setXpReward(random.nextInt(6) + 5); // 5-10 XP
        survey.setMinimumTimeSeconds(random.nextInt(180) + 60); // 1-4 minutes

        return survey;
    }

    private void createQuestionsForSurvey(Survey survey) {
        int questionCount = random.nextInt(4) + 3; // 3-6 questions per survey

        String[][] questionTemplates = {
                {"What is your age group?", "SINGLE_CHOICE", "18-24,25-34,35-44,45-54,55+"},
                {"What is your primary occupation?", "SINGLE_CHOICE", "Student,Employee,Freelancer,Unemployed,Retired"},
                {"Which of the following do you use regularly?", "MULTIPLE_CHOICE", "Smartphone,Laptop,Tablet,Desktop,Smart Watch"},
                {"How satisfied are you with our service?", "SINGLE_CHOICE", "Very Satisfied,Satisfied,Neutral,Dissatisfied,Very Dissatisfied"},
                {"What improvements would you suggest?", "TEXT", ""},
                {"Which social media platforms do you use?", "MULTIPLE_CHOICE", "Facebook,Instagram,Twitter,LinkedIn,TikTok,YouTube"},
                {"How often do you exercise per week?", "SINGLE_CHOICE", "Never,1-2 times,3-4 times,5-6 times,Daily"},
                {"What are your main concerns about the future?", "MULTIPLE_CHOICE", "Climate Change,Economy,Health,Technology,Education"},
                {"Describe your ideal work environment", "TEXT", ""},
                {"Rate your programming experience", "SINGLE_CHOICE", "Beginner,Intermediate,Advanced,Expert"}
        };

        for (int i = 0; i < questionCount; i++) {
            String[] template = questionTemplates[random.nextInt(questionTemplates.length)];

            Question question = new Question();
            question.setText(template[0]);
            question.setOrderIndex(i + 1);
            question.setType(Question.QuestionType.valueOf(template[1]));
            question.setRequired(random.nextBoolean());
            question.setSurvey(survey);

            Question savedQuestion = questionRepository.save(question);

            // Create options for choice questions
            if (!template[2].isEmpty() &&
                    (question.getType() == Question.QuestionType.SINGLE_CHOICE ||
                            question.getType() == Question.QuestionType.MULTIPLE_CHOICE)) {

                String[] options = template[2].split(",");
                for (int j = 0; j < options.length; j++) {
                    QuestionOption option = new QuestionOption();
                    option.setText(options[j].trim());
                    option.setOrderIndex(j + 1);
                    option.setQuestion(savedQuestion);
                    questionOptionRepository.save(option);
                }
            }
        }
    }

    private Forum createForumForSurvey(Survey survey) {
        Forum forum = new Forum();
        forum.setTitle("Discussion: " + survey.getTitle());
        forum.setDescription("Share your thoughts and discuss topics related to this survey");
        forum.setCreatedAt(LocalDateTime.now());
        forum.setEnabled(true);
        forum.setSurvey(survey);

        Forum savedForum = forumRepository.save(forum);

        // Update survey to reference the forum
        survey.setForum(savedForum);
        surveyRepository.save(survey);

        return savedForum;
    }

    private void createForumContent(Forum forum, List<AppUser> participants) {
        int subjectCount = random.nextInt(4) + 2; // 2-5 subjects per forum

        String[] subjectTitles = {
                "What are your thoughts on this topic?",
                "Interesting findings from the survey",
                "Similar experiences",
                "Questions about the methodology",
                "Suggestions for improvement",
                "Related research and studies",
                "Personal insights and observations",
                "Discussion on results interpretation"
        };

        for (int i = 0; i < subjectCount; i++) {
            AppUser randomParticipant = participants.get(random.nextInt(participants.size()));

            Subject subject = new Subject();
            subject.setTitle(subjectTitles[random.nextInt(subjectTitles.length)]);
            subject.setPostedAt(LocalDateTime.now().minusHours(random.nextInt(72)));
            subject.setCreator(randomParticipant);
            subject.setForum(forum);

            Subject savedSubject = subjectRepository.save(subject);

            // Create comments for each subject
            createCommentsForSubject(savedSubject, participants);
        }
    }

    private void createCommentsForSubject(Subject subject, List<AppUser> participants) {
        int commentCount = random.nextInt(6) + 1; // 1-6 comments per subject

        String[] comments = {
                "This is a very interesting perspective. I hadn't considered this angle before.",
                "I completely agree with the points raised in this survey.",
                "My experience has been quite different. Here's what I observed...",
                "Great question! I think this deserves more detailed analysis.",
                "Thanks for sharing this survey. The results are quite revealing.",
                "I have some concerns about the methodology used here.",
                "This aligns with what I've read in recent research papers.",
                "Interesting findings! Would love to see more data on this topic.",
                "I participated in this survey and found it very comprehensive.",
                "The results don't surprise me at all. This confirms my expectations."
        };

        for (int i = 0; i < commentCount; i++) {
            AppUser randomParticipant = participants.get(random.nextInt(participants.size()));

            Comment comment = new Comment();
            comment.setContent(comments[random.nextInt(comments.length)]);
            comment.setSentDate(subject.getPostedAt().plusMinutes(random.nextInt(60 * 24)));
            comment.setCreator(randomParticipant);
            comment.setSubject(subject);

            commentRepository.save(comment);
        }
    }

    private void createSurveyResponses(Survey survey, List<AppUser> participants) {
        // Random number of participants respond to each survey (30-80% of participants)
        int responseCount = (int) (participants.size() * (0.3 + random.nextDouble() * 0.5));
        Collections.shuffle(participants);

        List<Question> questions = questionRepository.findBySurveyOrderByOrderIndexAsc(survey);

        for (int i = 0; i < responseCount; i++) {
            AppUser participant = participants.get(i);

            // Create survey response
            SurveyResponse surveyResponse = new SurveyResponse();
            surveyResponse.setSurvey(survey);
            surveyResponse.setUser(participant);
            surveyResponse.setStartedAt(LocalDateTime.now().minusHours(random.nextInt(48)));
            surveyResponse.setCompletedAt(surveyResponse.getStartedAt().plusMinutes(random.nextInt(30) + 5));
            surveyResponse.setTimeSpentSeconds(random.nextInt(1800) + 300); // 5-35 minutes
            surveyResponse.setFlaggedAsSuspicious(random.nextDouble() < 0.05); // 5% flagged
            surveyResponse.setXpAwarded(surveyResponse.isFlaggedAsSuspicious() ? 0 : survey.getXpReward());

            SurveyResponse savedResponse = surveyResponseRepository.save(surveyResponse);

            // Create question responses
            for (Question question : questions) {
                if (random.nextDouble() < 0.9) { // 90% response rate per question
                    createQuestionResponse(question, savedResponse);
                }
            }
        }
    }

    private void createQuestionResponse(Question question, SurveyResponse surveyResponse) {
        QuestionResponse questionResponse = new QuestionResponse();
        questionResponse.setQuestion(question);
        questionResponse.setSurveyResponse(surveyResponse);

        switch (question.getType()) {
            case TEXT:
                String[] textResponses = {
                        "This is my detailed response to the question.",
                        "I think this topic is very important and needs more attention.",
                        "Based on my experience, I would say that...",
                        "I have mixed feelings about this subject.",
                        "This question made me think deeply about the issue.",
                        "I believe we need more research on this topic.",
                        "My personal opinion is that this varies by individual.",
                        "This is a complex issue with multiple perspectives."
                };
                questionResponse.setTextResponse(textResponses[random.nextInt(textResponses.length)]);
                break;

            case SINGLE_CHOICE:
                List<QuestionOption> options = questionOptionRepository.findByQuestionOrderByOrderIndexAsc(question);
                if (!options.isEmpty()) {
                    QuestionOption selectedOption = options.get(random.nextInt(options.size()));
                    questionResponse.setSelectedOptions(Arrays.asList(selectedOption));
                }
                break;

            case MULTIPLE_CHOICE:
                List<QuestionOption> allOptions = questionOptionRepository.findByQuestionOrderByOrderIndexAsc(question);
                if (!allOptions.isEmpty()) {
                    List<QuestionOption> selectedOptions = new ArrayList<>();
                    int selectCount = random.nextInt(Math.min(3, allOptions.size())) + 1;
                    Collections.shuffle(allOptions);

                    for (int i = 0; i < selectCount; i++) {
                        selectedOptions.add(allOptions.get(i));
                    }
                    questionResponse.setSelectedOptions(selectedOptions);
                }
                break;
        }

        questionResponseRepository.save(questionResponse);
    }
}