package analytics.controller;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.Scanner;

import analytics.EmptyInputException;
import analytics.InvalidPasswordLengthException;
import analytics.UsernameExistedException;
import analytics.ExistedPostIDException;
import analytics.InvalidNegativeIntegerException;

import analytics.model.Database;
import analytics.model.Post;
import analytics.model.User;
import analytics.view.EditProfileViewer;
import analytics.view.LoginViewer;
import analytics.InvalidNonPositiveIntegerException;
import analytics.InvalidContentException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class DashboardController {

    private Stage primaryStage;

    private User loginUser;

    private Database dataBase;

    @FXML
    private Label welcomeMessage;

    @FXML
    private TextField addPostIDInputField;

    @FXML
    private TextField addPostLikesInputField;

    @FXML
    private TextField addPostSharesInputField;

    @FXML
    private Label addPostAuthorLabelField;

    @FXML
    private TextField addPostContentInputField;

    @FXML
    private TextField addPostDateTimeInputField;

    @FXML
    private TextField retrievePostIDInputField;

    @FXML
    private TableView<Post> retrievePostTableView;
    @FXML
    private TableColumn<Post, Integer> retrievePostIDColumn;
    @FXML
    private TableColumn<Post, String> retrievePostContentColumn;
    @FXML
    private TableColumn<Post, String> retrievePostAuthorColumn;
    @FXML
    private TableColumn<Post, Integer> retrievePostLikesColumn;
    @FXML
    private TableColumn<Post, Integer> retrievePostSharesColumn;
    @FXML
    private TableColumn<Post, String> retrievePostDateTimeColumn;

    @FXML
    private TextField removePostIDInputField;

    @FXML
    private TextField retrieveTopNLikesPostNumberInputField;

    @FXML
    private ChoiceBox<String> retrieveTopNLikesPostAuthorChoiceBox;

    @FXML
    private Label NumPostExceedDatabaseLabel;

    @FXML
    private Label TopNLikesPostLabel;

    @FXML
    private TableView<Post> retrieveTopNLikesPostTableView;
    @FXML
    private TableColumn<Post, Integer> retrieveTopNLikesPostIDColumn;
    @FXML
    private TableColumn<Post, String> retrieveTopNLikesPostContentColumn;
    @FXML
    private TableColumn<Post, String> retrieveTopNLikesPostAuthorColumn;
    @FXML
    private TableColumn<Post, Integer> retrieveTopNLikesPostLikesColumn;
    @FXML
    private TableColumn<Post, Integer> retrieveTopNLikesPostSharesColumn;
    @FXML
    private TableColumn<Post, String> retrieveTopNLikesPostDateTimeColumn;

    public DashboardController() {
	dataBase = new Database();
    }

    public void setPrimaryStage(Stage primaryStage) {
	this.primaryStage = primaryStage;
    }

    public void initaliseUser(User loginUser) {
	this.loginUser = loginUser;
	addPostAuthorLabelField.setText(this.loginUser.getUsername());

	ObservableList<String> authorList = dataBase.retreieveAllUsersName();
	authorList.add("All Users");
	retrieveTopNLikesPostAuthorChoiceBox.setItems(authorList);
    }

    public void displayWelcomeMessage() {
	welcomeMessage.setText(String.format("Welcome, %s %s", loginUser.getFirstName(), loginUser.getLastName()));
    }

    @FXML
    public void redirectEditProfilePageHandler(ActionEvent event) {
	try {
	    EditProfileViewer editProfileViewer = new EditProfileViewer();
	    editProfileViewer.setPrimaryStage(primaryStage);
	    primaryStage.setTitle(editProfileViewer.getTitle());
	    primaryStage.setScene(editProfileViewer.getScene(loginUser));
	    primaryStage.setResizable(false);
	} catch (IOException e) {
	    Alert fileLoadingErrorAlert = new Alert(AlertType.ERROR);
	    fileLoadingErrorAlert.setHeaderText("Fail loading EditProfileView.fxml");
	    fileLoadingErrorAlert.setContentText("EditProfileView.fxml file path is not found");
	    fileLoadingErrorAlert.show();
	}
    }

    @FXML
    public void logOutUserHandler(ActionEvent event) {
	Alert logOutSuccessAlert = new Alert(AlertType.INFORMATION);
	logOutSuccessAlert.setHeaderText("You are now logged out.");
	logOutSuccessAlert.setContentText("Click OK to proceed to login.");
	logOutSuccessAlert.showAndWait();
	redirectLoginPage(event);
    }

    public void redirectLoginPage(ActionEvent event) {
	LoginViewer loginViewer = new LoginViewer();

	loginViewer.setPrimaryStage(primaryStage);
	primaryStage.setTitle(loginViewer.getTitle());

	try {
	    primaryStage.setScene(loginViewer.getScene());
	} catch (IOException e) {
	    Alert fileLoadingErrorAlert = new Alert(AlertType.ERROR);
	    fileLoadingErrorAlert.setHeaderText("Fail loading LoginView.fxml");
	    fileLoadingErrorAlert.setContentText("LoginView.fxml file path is not found");
	    fileLoadingErrorAlert.show();
	}

	primaryStage.setResizable(false);
    }

    @FXML
    public void addPostHandler(ActionEvent event) {

	try {
	    String id = addPostIDInputField.getText();
	    String postAuthor = addPostAuthorLabelField.getText(); // post author
	    String likes = addPostLikesInputField.getText();
	    String shares = addPostSharesInputField.getText();
	    String content = addPostContentInputField.getText();
	    String dateTime = addPostDateTimeInputField.getText();

	    int postId = readInputPostID(id); // post ID
	    int postLikes = readInputNonNegativeInt(likes); // post number of like
	    int postShares = readInputNonNegativeInt(shares); // post number of share
	    String postContent = readInputContent(content); // post content
	    String postDateTime = readInputDateTime(dateTime); // post date-time of creation

	    dataBase.createPost(new Post(postId, postContent, postAuthor, postLikes, postShares, postDateTime));

	    Alert addPostSuccess = new Alert(AlertType.INFORMATION);
	    addPostSuccess.setHeaderText("Add Post Success. Your new post is now saved.");
	    addPostSuccess.setContentText("Click OK to go back to dashboard.");
	    addPostSuccess.showAndWait();

	    addPostIDInputField.setText("");
	    addPostLikesInputField.setText("");
	    addPostSharesInputField.setText("");
	    addPostContentInputField.setText("");
	    addPostDateTimeInputField.setText("");
	} catch (EmptyInputException inputEmptyError) {
	    Alert inputEmptyErrorAlert = new Alert(AlertType.ERROR);
	    inputEmptyErrorAlert.setHeaderText("Add Post Failed");
	    inputEmptyErrorAlert.setContentText(inputEmptyError.getMessage());
	    inputEmptyErrorAlert.show();
	} catch (ExistedPostIDException postIDExisted) {
	    Alert PostIDExistedAlert = new Alert(AlertType.ERROR);
	    PostIDExistedAlert.setHeaderText("Add Post Failed");
	    PostIDExistedAlert.setContentText(postIDExisted.getMessage());
	    PostIDExistedAlert.show();
	} catch (NumberFormatException numberFormatError) {
	    Alert numberFormatErrorAlert = new Alert(AlertType.ERROR);
	    numberFormatErrorAlert.setHeaderText("Add Post Failed");
	    numberFormatErrorAlert.setContentText("Input must be an integer value.");
	    numberFormatErrorAlert.show();
	} catch (InvalidNegativeIntegerException integerNegativeError) {
	    Alert integerNegativeErrorAlert = new Alert(AlertType.ERROR);
	    integerNegativeErrorAlert.setHeaderText("Add Post Failed");
	    integerNegativeErrorAlert.setContentText(integerNegativeError.getMessage());
	    integerNegativeErrorAlert.show();
	} catch (InvalidContentException contentFormatError) {
	    Alert integerNegativeErrorAlert = new Alert(AlertType.ERROR);
	    integerNegativeErrorAlert.setHeaderText("Add Post Failed");
	    integerNegativeErrorAlert.setContentText(contentFormatError.getMessage());
	    integerNegativeErrorAlert.show();
	} catch (ParseException parseError) {
	    Alert parseErrorAlert = new Alert(AlertType.ERROR);
	    parseErrorAlert.setHeaderText("Add Post Failed");
	    parseErrorAlert.setContentText(
		    "Invalid date-time value or/and format. The date-time must be in the format of DD/MM/YYYY HH:MM.");
	    parseErrorAlert.show();
	}

    }

    @FXML
    public void retrievePostHandler(ActionEvent event) {
	try {
	    retrievePostTableView.getItems().clear();

	    String id = retrievePostIDInputField.getText();

	    Post post = readInputRetrievePostID(id);

	    if (post != null) {
		retrievePostTableView.getItems().add(post);
		retrievePostIDColumn.setCellValueFactory(new PropertyValueFactory<Post, Integer>("id"));
		retrievePostContentColumn.setCellValueFactory(new PropertyValueFactory<Post, String>("content"));
		retrievePostAuthorColumn.setCellValueFactory(new PropertyValueFactory<Post, String>("author"));
		retrievePostLikesColumn.setCellValueFactory(new PropertyValueFactory<Post, Integer>("likes"));
		retrievePostSharesColumn.setCellValueFactory(new PropertyValueFactory<Post, Integer>("shares"));
		retrievePostDateTimeColumn.setCellValueFactory(new PropertyValueFactory<Post, String>("dateTime"));
		retrievePostIDInputField.setText("");
	    } else {
		Alert postNotExistAlert = new Alert(AlertType.ERROR);
		postNotExistAlert.setHeaderText("Retreive Post Failed");
		postNotExistAlert.setContentText("Sorry the post does not exist in the database!");
		postNotExistAlert.show();
	    }
	} catch (EmptyInputException inputEmptyError) {
	    Alert inputEmptyErrorAlert = new Alert(AlertType.ERROR);
	    inputEmptyErrorAlert.setHeaderText("Retreive Post Failed");
	    inputEmptyErrorAlert.setContentText(inputEmptyError.getMessage());
	    inputEmptyErrorAlert.show();
	} catch (NumberFormatException numberFormatError) {
	    Alert numberFormatErrorAlert = new Alert(AlertType.ERROR);
	    numberFormatErrorAlert.setHeaderText("Retreive Post Failed");
	    numberFormatErrorAlert.setContentText("Input must be an integer value.");
	    numberFormatErrorAlert.show();
	} catch (InvalidNegativeIntegerException integerNegativeError) {
	    Alert integerNegativeErrorAlert = new Alert(AlertType.ERROR);
	    integerNegativeErrorAlert.setHeaderText("Retreive Post Failed");
	    integerNegativeErrorAlert.setContentText(integerNegativeError.getMessage());
	    integerNegativeErrorAlert.show();
	}
    }

    @FXML
    public void removePostHandler(ActionEvent event) {
	try {
	    String id = removePostIDInputField.getText();

	    Post post = readInputRetrievePostID(id);

	    if (post != null) {
		dataBase.removePost(post.getId());
		Alert removePostSuccess = new Alert(AlertType.INFORMATION);
		removePostSuccess.setHeaderText("Remove Post Success");
		removePostSuccess.setContentText("The post is successfully removed from the database!");
		removePostSuccess.showAndWait();
		removePostIDInputField.setText("");
	    } else {
		Alert postNotExistAlert = new Alert(AlertType.ERROR);
		postNotExistAlert.setHeaderText("Remove Post Failed");
		postNotExistAlert.setContentText("Sorry the post does not exist in the database!");
		postNotExistAlert.show();
	    }
	} catch (EmptyInputException inputEmptyError) {
	    Alert inputEmptyErrorAlert = new Alert(AlertType.ERROR);
	    inputEmptyErrorAlert.setHeaderText("Remove Post Failed");
	    inputEmptyErrorAlert.setContentText(inputEmptyError.getMessage());
	    inputEmptyErrorAlert.show();
	} catch (NumberFormatException numberFormatError) {
	    Alert numberFormatErrorAlert = new Alert(AlertType.ERROR);
	    numberFormatErrorAlert.setHeaderText("Remove Post Failed");
	    numberFormatErrorAlert.setContentText("Input must be an integer value.");
	    numberFormatErrorAlert.show();
	} catch (InvalidNegativeIntegerException integerNegativeError) {
	    Alert integerNegativeErrorAlert = new Alert(AlertType.ERROR);
	    integerNegativeErrorAlert.setHeaderText("Remove Post Failed");
	    integerNegativeErrorAlert.setContentText(integerNegativeError.getMessage());
	    integerNegativeErrorAlert.show();
	}
    }

    @FXML
    public void retrieveTopNLikesPostHandler(ActionEvent event) {
	try {
	    retrieveTopNLikesPostTableView.getItems().clear();
	    NumPostExceedDatabaseLabel.setText("");
	    TopNLikesPostLabel.setText("");

	    String strNumberPost = retrieveTopNLikesPostNumberInputField.getText();
	    String selectedAuthor = retrieveTopNLikesPostAuthorChoiceBox.getValue();

	    int intNumberPost = readInputPositiveInt(strNumberPost);

	    if (selectedAuthor != null) {
		PriorityQueue<Post> topNLikesPost = dataBase.retrieveTopNLikesPost(selectedAuthor);

		if (topNLikesPost.size() == 0) {
		    NumPostExceedDatabaseLabel
			    .setText(String.format("0 post exist in the database for %s.", selectedAuthor));
		} else {
		    if (topNLikesPost.size() < intNumberPost) {
			intNumberPost = topNLikesPost.size();
			NumPostExceedDatabaseLabel.setText(
				String.format("Only %d posts exist in the database for %s. Showing all of them.",
					intNumberPost, selectedAuthor));
		    }
		    TopNLikesPostLabel
			    .setText(String.format("The top-%d posts with the most likes are:", intNumberPost));
		}

		int i = 0;
		while ((!topNLikesPost.isEmpty()) && (i < intNumberPost)) {
		    retrieveTopNLikesPostTableView.getItems().add(topNLikesPost.poll());
		    i++;
		}
		retrieveTopNLikesPostIDColumn.setCellValueFactory(new PropertyValueFactory<Post, Integer>("id"));
		retrieveTopNLikesPostContentColumn
			.setCellValueFactory(new PropertyValueFactory<Post, String>("content"));
		retrieveTopNLikesPostAuthorColumn.setCellValueFactory(new PropertyValueFactory<Post, String>("author"));
		retrieveTopNLikesPostLikesColumn.setCellValueFactory(new PropertyValueFactory<Post, Integer>("likes"));
		retrieveTopNLikesPostSharesColumn
			.setCellValueFactory(new PropertyValueFactory<Post, Integer>("shares"));
		retrieveTopNLikesPostDateTimeColumn
			.setCellValueFactory(new PropertyValueFactory<Post, String>("dateTime"));

		retrieveTopNLikesPostNumberInputField.setText("");
		retrieveTopNLikesPostAuthorChoiceBox.setValue(null);

		;
	    } else {
		Alert selectedAuthorChoiceEmptyErrorAlert = new Alert(AlertType.ERROR);
		selectedAuthorChoiceEmptyErrorAlert.setHeaderText("Retreive Top N Likes Post Failed");
		selectedAuthorChoiceEmptyErrorAlert.setContentText("Please select an author!");
		selectedAuthorChoiceEmptyErrorAlert.show();
	    }
	} catch (EmptyInputException inputEmptyError) {
	    Alert inputEmptyErrorAlert = new Alert(AlertType.ERROR);
	    inputEmptyErrorAlert.setHeaderText("Retreive Top N Likes Post Failed");
	    inputEmptyErrorAlert.setContentText(inputEmptyError.getMessage());
	    inputEmptyErrorAlert.show();
	} catch (NumberFormatException numberFormatError) {
	    Alert numberFormatErrorAlert = new Alert(AlertType.ERROR);
	    numberFormatErrorAlert.setHeaderText("Retreive Top N Likes Post Failed");
	    numberFormatErrorAlert.setContentText("Input must be an integer value.");
	    numberFormatErrorAlert.show();
	} catch (InvalidNonPositiveIntegerException integerNonPositiveError) {
	    Alert integerNonPositiveErrorAlert = new Alert(AlertType.ERROR);
	    integerNonPositiveErrorAlert.setHeaderText("Retreive Top N Likes Post Failed");
	    integerNonPositiveErrorAlert.setContentText(integerNonPositiveError.getMessage());
	    integerNonPositiveErrorAlert.show();
	}
    }

    private Post readInputRetrievePostID(String input)
	    throws EmptyInputException, InvalidNegativeIntegerException, NumberFormatException {
	int postID = 0;
	Post post;
	try {
	    input = input.trim();
	    postID = readInputNonNegativeInt(input);
	    post = dataBase.retrievePost(postID);
	} catch (NumberFormatException e) {
	    throw new NumberFormatException();
	} catch (EmptyInputException e) {
	    throw new EmptyInputException();
	} catch (InvalidNegativeIntegerException e) {
	    throw new InvalidNegativeIntegerException();
	}

	return post;
    }

    /**
     * The method to read user's post ID input and call readInputNonNegativeInt to
     * validate the parsed integer format and check if post ID already existed in
     * database in order to return the non-exist post ID integer.
     * 
     * @param text The text to be print to prompt user's input
     * @return the the non-exist post ID integer input
     * @throws ExistedPostIDException
     * @throws EmptyInputException
     * @throws InvalidNegativeIntegerException
     */
    private int readInputPostID(String input)
	    throws ExistedPostIDException, EmptyInputException, InvalidNegativeIntegerException {
	int postID = 0;
	try {
	    input = input.trim();
	    postID = readInputNonNegativeInt(input);
	    dataBase.checkPostIDExist(postID);
	} catch (NumberFormatException e) {
	    throw new NumberFormatException();
	} catch (EmptyInputException e) {
	    throw new EmptyInputException();
	} catch (InvalidNegativeIntegerException e) {
	    throw new InvalidNegativeIntegerException();
	} catch (ExistedPostIDException postIDExisted) {
	    throw new ExistedPostIDException(postID);
	}

	return postID;
    }

    /**
     * The method to read post's content input and validate the content format in
     * order to return the valid content.
     * 
     * @param text        The text to be print to prompt user's input
     * @param inputStream The source which the input will be read from
     * @return the valid post's content input
     * @throws EmptyInputException
     * @throws InvalidContentException
     */
    private String readInputContent(String input) throws EmptyInputException, InvalidContentException {
	try {
	    input = input.trim();
	    checkInputEmpty(input);
	    checkContentFormat(input);
	} catch (EmptyInputException inputEmptyError) {
	    throw new EmptyInputException();
	} catch (InvalidContentException contentFormatError) {
	    throw new InvalidContentException();
	}
	return input;
    }

    /**
     * The method to read user's string input and validate the parsed integer format
     * in order to return the valid non-negative parsed integer.
     * 
     * @param text        The text to be print to prompt user's input
     * @param inputStream The source which the input will be read from
     * @return the valid parsed non-negative integer input
     * @throws EmptyInputException
     * @throws InvalidNegativeIntegerException
     */
    private int readInputNonNegativeInt(String input)
	    throws EmptyInputException, NumberFormatException, InvalidNegativeIntegerException {
	int intInput = 0;
	try {
	    checkInputEmpty(input);
	    intInput = Integer.parseInt(input);
	    checkNonNegativeIntegerFormat(intInput);
	} catch (EmptyInputException inputEmptyError) {
	    throw new EmptyInputException();
	} catch (NumberFormatException numberFormatError) {
	    throw new NumberFormatException();
	} catch (InvalidNegativeIntegerException integerNegativeError) {
	    throw new InvalidNegativeIntegerException();
	}

	return intInput;
    }

    private int readInputPositiveInt(String input)
	    throws EmptyInputException, NumberFormatException, InvalidNonPositiveIntegerException {
	int intInput = 0;
	try {
	    input = input.trim();
	    checkInputEmpty(input);
	    intInput = Integer.parseInt(input);
	    checkPositiveIntegerFormat(intInput);
	} catch (EmptyInputException inputEmptyError) {
	    throw new EmptyInputException();
	} catch (NumberFormatException numberFormatError) {
	    throw new NumberFormatException();
	} catch (InvalidNonPositiveIntegerException integerNonPositiveError) {
	    throw new InvalidNonPositiveIntegerException();
	}

	return intInput;
    }

    /**
     * The method to check if integer is negative or zero to throw user-defined
     * InvalidNegativeIntegerException
     * 
     * @param integer The integer to be validate
     */
    private void checkPositiveIntegerFormat(int integer) throws InvalidNonPositiveIntegerException {
	if (integer <= 0) {
	    throw new InvalidNonPositiveIntegerException();
	}
    }

    /**
     * The method to read post's date-time input and validate the date-time format
     * in order to return the valid date-time.
     * 
     * @param text        The text to be print to prompt user's input
     * @param inputStream The source which the input will be read from
     * @return the valid post's date-time format
     * @throws EmptyInputException
     */
    private String readInputDateTime(String input) throws EmptyInputException, ParseException {
	// Note that SimpleDateFormat class and methods are adapted from:
	// https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	dateFormat.setLenient(false);
	try {
	    input = input.trim();
	    checkInputEmpty(input);
	    Date date = dateFormat.parse(input);
	    input = dateFormat.format(date);
	} catch (EmptyInputException inputEmptyError) {
	    throw new EmptyInputException();
	} catch (ParseException parseError) {
	    throw new ParseException(input, 0);
	}
	return input;
    }

    /**
     * The method to check if input is empty to throw user-defined
     * EmptyContentException
     * 
     * @param content The string to be validate
     */
    private void checkInputEmpty(String input) throws EmptyInputException {
	if (input.isEmpty()) {
	    throw new EmptyInputException();
	}
    }

    /**
     * The method to check if content contains "," to throw user-defined
     * InvalidContentException
     * 
     * @param content The string to be validate
     */
    private void checkContentFormat(String content) throws InvalidContentException {
	if (content.contains(",")) {
	    throw new InvalidContentException();
	}
    }

    /**
     * The method to check if integer is negative to throw user-defined
     * InvalidNegativeIntegerException
     * 
     * @param integer The integer to be validate
     */
    private void checkNonNegativeIntegerFormat(int integer) throws InvalidNegativeIntegerException {
	if (integer < 0) {
	    throw new InvalidNegativeIntegerException();
	}
    }
}
