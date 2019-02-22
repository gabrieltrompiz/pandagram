import React from 'react'
import { Grid, Icon, Container } from 'semantic-ui-react'
import LoginCard from '../components/LoginCard'
import RegisterCard from '../components/RegisterCard'
const background = require('../assets/WWF_logo1.png')

export default class LoginView extends React.Component {
    constructor(props) {
        super(props)
        this.state = { loginVisible: false, registerVisible: true }
    }

    handleChangeCard = card => {
        if(card === 'register') {
            this.setState({ loginVisible: false })
            setTimeout(() => this.setState({ registerVisible: true}), 400)
        }
        else {
            this.setState({ registerVisible: false })
            setTimeout(() => this.setState({ loginVisible: true}), 400)
        }
    } 

    render(){
        return(
            <Grid columns={2} divided style={{ height: 'inherit' }}>
                <Grid.Column style={{ textAlign: 'center', backgroundImage: `url(${background})`, backgroundSize: 'cover', backgroundRepeat: 'no-repeat', backgroundPosition: 'center', }}>
                    <div style={{ marginTop: '35vh' }}>
                        <Icon name="search" style={{ color: 'white', fontSize: 40 }} />
                        <span style={{ fontFamily: "Roboto", fontSize: 24, paddingLeft: 15, color: 'white', paddingBottom: 10 }}>Search your friends.</span>       
                    </div>
                    <div style={{ marginTop: 30 }}>
                        <Icon name="users" style={{ color: 'white', fontSize: 40 }} />
                        <span style={{ fontFamily: "Roboto", fontSize: 24, paddingLeft: 15, color: 'white', paddingBottom: 10 }}>See what your friends are posting.</span>       
                    </div>
                    <div style={{ marginTop: 30 }}>
                        <Icon name="comment" style={{ color: 'white', fontSize: 40 }} />
                        <span style={{ fontFamily: "Roboto", fontSize: 24, paddingLeft: 15, color: 'white', paddingBottom: 10 }}>Join the conversation.</span>       
                    </div>   
                </Grid.Column>
                <Grid.Column>
                    <Container fluid>
                        <LoginCard changeCard={this.handleChangeCard} visible={this.state.loginVisible}/>
                        <RegisterCard changeCard={this.handleChangeCard} visible={this.state.registerVisible} />
                    </Container>
                </Grid.Column>
            </Grid>
        );
    }
}