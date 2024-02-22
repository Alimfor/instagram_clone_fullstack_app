import {Button, Col, Form, Input, notification, Row} from "antd";
import React, {Component} from "react";
import {signup} from "../../util/ApiUtil";
import Link from "antd/es/typography/Link";
import {
    EMAIL_MAX_LENGTH,
    NAME_MAX_LENGTH,
    NAME_MIN_LENGTH, PASSWORD_MAX_LENGTH, PASSWORD_MIN_LENGTH,
    USERNAME_MAX_LENGTH,
    USERNAME_MIN_LENGTH
} from "../../common/constants";

const FormItem = Form.Item;

class Signup extends Component {

    state = {
        name: {
            value: ""
        },
        username: {
            value: ""
        },
        email: {
            value: ""
        },
        password: {
            value: ""
        }
    };

    componentDidMount = () => {

        if (this.props.isAuthenticated) {
            this.props.history.push("/");
        }
    }

    handleSubmit = e => {

        e.preventDefault();

        const signupRequest = {

            name: this.state.name.value,
            username: this.state.username.value,
            email: this.state.email.value,
            password: this.state.password.value
        };

        signup(signupRequest)
            .then(response => {
                notification.success({
                    message: "Signup",
                    description: "You have successfully signed up. Please login."
                });
                this.props.history.push("/login");
            })
            .catch(error => {

                notification.error({
                    message: "Signup",
                    description:
                        error.message ||
                        "Sorry! Something went wrong. Please try again."
                });
            });
    }

    handleInputChange = (key, value) => {

        const target = key.target;
        const inputName = target.name;
        const inputValue = target.value;

        this.setState({
            [inputName]: {
                value: inputValue,
                ...value(inputValue)
            }
        });
    }

    isFromInvalid() {
        return !(
            this.state.name.validateStatus === "success" &&
            this.state.username.validateStatus === "success" &&
            this.state.email.validateStatus === "success" &&
            this.state.password.validateStatus === "success"
        );
    }

    render() {
        return (
            <React.Fragment>
                <div className="signup">
                    <Row
                        type="flex"
                        justify="center"
                    >
                        <Col span={24}>
                            <div className="logo">
                                <span>Instagram Clone</span>
                            </div>
                        </Col>
                        <Col span={24}>
                            <Form
                                onSubmit={this.handleSubmit}
                                className="signup-form"
                            >
                                <FormItem
                                    validateStatus={this.state.name.validateStatus}
                                    help={this.state.name.errorMsg}
                                    hasFeedback
                                >
                                    <Input
                                        size="large"
                                        name="name"
                                        placeholder="Name"
                                        value={this.state.name.value}
                                        onChange={event =>
                                            this.handleInputChange(event, this.validateName)
                                        }
                                    />
                                </FormItem>
                                <FormItem
                                    validateStatus={this.state.username.validateStatus}
                                    help={this.state.username.errorMsg}
                                    hasFeedback
                                >
                                    <Input
                                        size="large"
                                        name="username"
                                        placeholder="Username"
                                        value={this.state.username.value}
                                        onChange={event =>
                                            this.handleInputChange(event, this.validateUsername)
                                        }
                                    />
                                </FormItem>
                                <FormItem
                                    validateStatus={this.state.email.validateStatus}
                                    help={this.state.email.errorMsg}
                                    hasFeedback
                                >
                                    <Input
                                        size="large"
                                        name="email"
                                        placeholder="Email"
                                        value={this.state.email.value}
                                        onChange={event =>
                                            this.handleInputChange(event, this.validateEmail)
                                        }
                                    />
                                </FormItem>
                                <FormItem
                                    validateStatus={this.state.password.validateStatus}
                                    help={this.state.password.errorMsg}
                                    hasFeedback
                                >
                                    <Input
                                        size="large"
                                        name="password"
                                        placeholder="Password"
                                        value={this.state.password.value}
                                        onChange={event =>
                                            this.handleInputChange(event, this.validatePassword)
                                        }
                                    />
                                </FormItem>
                                <FormItem>
                                    <Button
                                        type="primary"
                                        htmlType="submit"
                                        size="large"
                                        className="signup-form-button"
                                        disabled={this.isFromInvalid()}
                                    >
                                        Signup
                                    </Button>
                                </FormItem>
                            </Form>
                        </Col>
                    </Row>
                </div>
                <div className="login-link">
                    Have an account? <Link to="/login">Login</Link>
                </div>
            </React.Fragment>
        )
    }

    validateName = name => {

        if (!name) {
            return {
                validateStatus: "warning",
                errorMsg: "Please input your name."
            }
        }

        if (name.length < NAME_MIN_LENGTH) {
            return {
                validateStatus: "error",
                errorMsg: `Name should not be less than ${NAME_MIN_LENGTH} characters.`
            }
        } else if (name.length > NAME_MAX_LENGTH) {
            return {
                validateStatus: "error",
                errorMsg: `Name should not be more than ${NAME_MAX_LENGTH} characters.`
            }
        }

        return {
            validateStatus: "success",
            errorMsg: null
        }
    }

    validateEmail = email => {

        if (!email) {
            return {
                validateStatus: "warning",
                errorMsg: "Please input your email."
            }
        }

        if (email.length > EMAIL_MAX_LENGTH) {
            return {
                validateStatus: "error",
                errorMsg: `Email should not be more than ${EMAIL_MAX_LENGTH} characters.`
            }
        }

        const EMAIL_REGEX = RegExp("[^@ ]+@[^@ ]+\\.[^@ ]+");
        if (!EMAIL_REGEX.test(email)) {
            return {
                validateStatus: "error",
                errorMsg: "Email not valid."
            }
        }

        return {
            validateStatus: "success",
            errorMsg: null
        }
    }

    validateUsername = username => {

        if (!username) {
            return {
                validateStatus: "warning",
                errorMsg: "Please input your username."
            }
        }

        if (username.length < USERNAME_MIN_LENGTH) {
            return {
                validateStatus: "error",
                errorMsg: `Username should not be less than ${USERNAME_MIN_LENGTH} characters.`
            }
        } else if (username.length > USERNAME_MAX_LENGTH) {
            return {
                validateStatus: "error",
                errorMsg: `Username should not be more than ${USERNAME_MAX_LENGTH} characters.`
            }
        }

        return {
            validateStatus: "success",
            errorMsg: null
        }
    }

    validatePassword = password => {

        if (!password) {
            return {
                validateStatus: "warning",
                errorMsg: "Please input your password."
            }
        }

        if (password.length < PASSWORD_MIN_LENGTH) {
            return {
                validateStatus: "error",
                errorMsg: `Password should not be less than ${PASSWORD_MIN_LENGTH} characters.`
            }
        } else if (password.length > PASSWORD_MAX_LENGTH) {
            return {
                validateStatus: "error",
                errorMsg: `Password should not be more than ${PASSWORD_MAX_LENGTH} characters.`
            }
        }

        return {
            validateStatus: "success",
            errorMsg: null
        }
    }
}

export default Signup;