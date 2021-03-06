import React from 'react';
import { Segment, Divider } from 'semantic-ui-react';
import Not from '../components/Not.js';

export default class Notifications extends React.Component {
	constructor(props) {
		super(props)
		this.state = { notifications: this.props.notifications }
	}

	componentWillReceiveProps = (nextProps) => {
		this.setState({ notifications: nextProps.notifications })
	}

	render() {
		const dark = this.props.darkTheme
		const styles = this.getStyles(dark)
		console.log("nots")
		console.log(this.state.notifications)
		return(
			<Segment raised style={{ width: '75%', height: '94vh', left: '20.5%', marginTop: '2.5vh', position: 'fixed', backgroundColor: dark ? '#15202B' : 'white' }}>
				<p style={styles.title}>Notifications</p>
				<Divider fitted style={{ marginTop: 2 }} />
				{this.state.notifications.length === 0 && 
				<div style={styles.empty}>
					<i className="fas fa-inbox" style={styles.icon}></i>
					<p style={styles.text}>You don't have any <br /> notifications.</p>
				</div>}
				{this.state.notifications.lenght !== 0 &&
				this.state.notifications.map((notification, i) => {
					return <Not notification={notification} key={i} darkTheme={dark} notificationSocket={this.props.notificationSocket} updateDashboard={this.props.updateDashboard} ownUser={this.props.user}/>
				})}
			</Segment>
		);
	}

	getStyles = (dark) => {
		const styles = { 
			title: {
				fontFamily: 'Heebo',
				fontSize: 30,
				fontWeight: 'bolder',
				margin: 0,
				color: dark ? 'white' : 'black'
			},
			empty: {
				height: '92%',
				width: '100%',
				display: 'flex',
				flexDirection: 'column',
				alignItems: 'center',
				justifyContent: 'center',
				marginTop: 10,
			},
			icon: {
				color: dark ? '#8899A6' : 'grey',
				fontSize: 40,
				opacity: 0.8
			},
			text: {
				color: dark ? '#8899A6' : 'grey', 
				opacity: 0.8,
				fontSize: 22,
				fontFamily: 'Heebo', 
				fontWeight: 'bolder',
				textAlign: 'center',
				lineHeight: 1.1,
				marginTop: 10
			}
		}
		return styles
	}
}

