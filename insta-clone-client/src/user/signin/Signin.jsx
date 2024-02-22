import {Button, Col, Form, Input, notification, Row} from "antd";
import React, {Component} from "react";
import {login} from "../../util/ApiUtil";
import {ACCESS_TOKEN} from "../../common/constants";
import Icon from "antd/es/icon";
import { Link } from "react-router-dom";

const FormItem = Form.Item;

class Signin extends Component {

    state = {};

    componentDidMount = () => {

        if (this.props.isAuthenticated) {
            this.props.history.push("/");
        }
    }

    render() {

        const AntWrappedLoginForm = Form.create()(SigninForm);

        return (
            <React.Fragment>
                <div className="signin">
                    <Row
                        type="flex"
                        justify="center"
                    >
                        <Col span={24}>
                            <div className="logo">
                                Instagram Clone
                            </div>
                        </Col>
                        <Col span={24}>
                            <AntWrappedLoginForm onLogin={this.props.onLogin}/>
                        </Col>
                    </Row>
                </div>
                <div className="signup-link">
                    Don't have an account? <Link to="/signup">Sign up</Link>
                </div>
            </React.Fragment>
        )
    }
}

class SigninForm extends Component {

    state = {};

    handleSubmit = (e) => {

        e.preventDefault();
        this.props.form.validateFields((err, values) => {

            if (!err) {

                const signinRequest = Object.assign({}, values);
                login(signinRequest)
                    .then(response => {
                        localStorage.setItem(ACCESS_TOKEN, response.accessToken);
                    })
                    .catch(error => {

                        if (error.status === 401) {
                            notification.error({
                                message: "Authentication failed",
                                description: "Username or password is incorrect"
                            });
                            return;
                        }
                        notification.error({
                            message: "Authentication failed",
                            description:
                                error.message ||
                                "Sorry! Something went wrong. Please try again!"
                        });
                    });
            }
        });
    }

    render() {

        const {getFieldDecorator} = this.props.form;

        return (
            <Form
                onSubmit={this.handleSubmit}
                className="signin-form"
            >
                <FormItem>
                    {getFieldDecorator("username", {
                        rules: [
                            {
                                required: true,
                                message: "Please input your username!"
                            }
                        ]
                    }) (
                        <Input
                            prefix={<Icon type="user"/>}
                            size="large"
                            name="username"
                            placeholder="Username"
                        />
                    )}
                </FormItem>
                <FormItem>
                    {getFieldDecorator("password", {
                        rules: [
                            {
                                required: true,
                                message: "Please input your password!"
                            }
                        ]
                    }) (
                        <Input
                            prefix={<Icon type="lock"/>}
                            size="large"
                            name="password"
                            type="password"
                            placeholder="Password"
                        />
                    )}
                </FormItem>
                <FormItem>
                    <Button
                        type="primary"
                        htmlType="submit"
                        size="large"
                        className="signin-form-button"
                    >
                        Sign in
                    </Button>
                </FormItem>
            </Form>
        );
    }
}

export default Signin;