import React from 'react'
import { Segment, Divider } from 'semantic-ui-react'
import Rep from '../components/Rep';

export default class Reports extends React.Component {
    constructor(props) {
        super(props)
        this.state = { reports: this.props.reports }
    }

    componentWillReceiveProps = (nextProps) => {
        if(nextProps.reports !== this.props.reports) {
            this.setState({ reports: nextProps.reports })
        }
    }

    render() {
        const dark = this.props.darkTheme
        const styles = this.getStyles(dark)
        return(
            <Segment raised style={{ width: '75%', height: '94vh', left: '20.5%', marginTop: '2.5vh', position: 'fixed', backgroundColor: dark ? '#15202B' : 'white' }}>
                <p style={styles.title}>Reports</p>
				<Divider fitted style={{ marginTop: 2 }} />
                {this.state.reports.length === 0 && 
				<div style={styles.empty}>
					<i className="fas fa-inbox" style={styles.icon}></i>
					<p style={styles.text}>You don't have any <br /> notifications.</p>
				</div>}
                {this.state.reports.length !== 0 &&
                this.state.reports.map((report, i) => {
                    return <Rep report={report} key={i} darkTheme={dark}/>
                })}
            </Segment>
        )
    }

    getStyles = dark => {
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