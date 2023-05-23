/**
 * HTML <strong><em>input</em></strong> element that contains the name.
 * @date 5/21/2023 - 3:24:18 PM
 *
 * @type {Object}
 */
const inputName = document.getElementsByName("name")[0];
/**
 * HTML <strong><em>input</em></strong> element that contains the username.
 * @date 5/21/2023 - 3:25:46 PM
 *
 * @type {Object}
 */
const inputUsername = document.getElementsByName("username")[0];
/**
 * HTML <strong><em>input</em></strong> element that contains the password.
 * @date 5/21/2023 - 3:26:34 PM
 *
 * @type {Object}
 */
const inputPassword = document.getElementsByName("password")[0];
/**
 * HTML <strong><em>input</em></strong> element that contains the password confirmation.
 * @date 5/21/2023 - 3:27:02 PM
 *
 * @type {Object}
 */
const inputConfirmPassword = document.getElementsByName("cpassword")[0];
/**
 * HTML <strong><em>form</em></strong> element that contains all the elements.
 * @date 5/21/2023 - 3:27:28 PM
 *
 * @type {Object}
 */
const form = document.getElementById("signup-edit-form");
/**
 * HTML <strong><em>div</em></strong> element that contains a paragraph for the error message.
 * @date 5/21/2023 - 3:28:25 PM
 *
 * @type {Object}
 */
const signUpError = document.getElementsByClassName("error-signup")[0];
/**
 * HTML <strong><em>div</em></strong> element that contains a paragraph for the success message.
 * @date 5/23/2023 - 9:15:30 AM
 *
 * @type {HTMLElement}
 */
const signUpSuccess = document.getElementsByClassName("success-signup")[0];
/**
 * HTML <strong><em>p</em></strong> element that displays the success message either for sign up or edit profile
 * @date 5/23/2023 - 1:28:18 PM
 *
 * @type {HTMLElement}
 */
const successMessage = document.getElementById("success-message");
/**
 * The way to get things of URL.
 * @date 5/23/2023 - 1:04:50 PM
 *
 * @type {URLSearchParams}
 */
const parameters = new URLSearchParams(window.location.search);
/**
 * UUID to be get from URL.
 * @date 5/23/2023 - 11:47:00 AM
 *
 * @type {string}
 */
const tokenParameter = parameters.get("token");
/**
 * The search key to be get from URL. (SIGNUP or PROFILE) 
 * @date 5/23/2023 - 11:48:02 AM
 *
 * @type {string}
 */
const keySearchParameter = parameters.get("key-search");

window.onload = () => {
    if (keySearchParameter == keySearchEnum.EDIT_PROFILE) {
        document.getElementsByTagName("h3")[0].innerText = "edit profile";
        document.getElementById("signin-message").classList.add("disappear");

        fetch(
            urlBase + "/user/data",
            fetchContentFactoryWithoutBody(requestMethods.GET, tokenParameter),
        ).then((response) => {
            if (response.ok) {
                return response.json();
            } else {
                console.error("Get own data failed");
            }
        }).then((user) => {
            inputName.value = user.name;
            inputUsername.value = user.username;
            inputUsername.disabled = true;
        });
    }
};
/**
 * Creates a new user in the system.
 * @date 5/21/2023 - 3:23:01 PM
 *
 * @param {string} password to be added into request body
 */
const signUp = (password) => {
    let inputNameValue = inputName.value.trim();
    let inputUsernameValue = inputUsername.value.trim();

    let body = {
        name: inputNameValue,
        username: inputUsernameValue,
        password: password,
        role: role.VISITOR
    }

    fetch(
        urlBase + "/user/signup", fetchContentFactoryWithBody(requestMethods.POST, body)
    ).then((response) => {
        if (response.ok) {
            return response.text();
        }
    }).then((userId) => {
        successMessage.innerText = "User registered successfully"
        signUpSuccess.classList.remove("disappear");
        setTimeout(() => {
            signUpSuccess.classList.add("disappear");

            dataURL.delete("key-search");
            dataURL.append("token", NOT_LOGGED_TOKEN);

            window.location.href = "home.html?" + dataURL.toString();
        }, 2000);
    });
};

/**
 * Verifies if both input field values have same value.
 * @date 5/21/2023 - 3:22:05 PM
 */
const verifyPasswords = () => {
    let inputPasswordValue = inputPassword.value.trim();
    let inputConfirmPasswordValue = inputConfirmPassword.value.trim();

    if (inputPasswordValue === inputConfirmPasswordValue) {
        keySearchParameter == keySearchEnum.EDIT_PROFILE ? editProfile(inputPasswordValue) : signUp(inputPasswordValue);

        inputName.value = "";
        inputUsername.value = "";
        inputPassword.value = "";
        inputConfirmPassword.value = "";
    } else {
        signUpError.classList.remove("disappear");
        inputPassword.value = "";
        inputConfirmPassword.value = "";
        setTimeout(() => {
            signUpError.classList.add("disappear");
        }, 2000);
    }
};

/**
 * Updates the logged user.
 * @date 5/23/2023 - 11:07:16 AM
 *
 * @param {string} password to be added into request body
 */
const editProfile = (password) => {
    let inputNameValue = inputName.value.trim();
    let body = {
        name: inputNameValue,
        password: password
    }

    fetch(
        urlBase + "/user/data", fetchContentFactoryWithBody(requestMethods.PUT, body, tokenParameter)
    ).then((response) => {
        if (response.ok) {
            return response.json();
        }
    }).then((updatedUser) => {
        successMessage.innerText = "User updated successfully"
        signUpSuccess.classList.remove("disappear");
        setTimeout(() => {
            signUpSuccess.classList.add("disappear");

            dataURL.delete("key-search");
            dataURL.append("token", tokenParameter);

            window.location.href = "home.html?" + dataURL.toString();
        }, 2000);
    });
};

form.addEventListener('submit', (event) => {
    event.preventDefault();
    verifyPasswords();
})